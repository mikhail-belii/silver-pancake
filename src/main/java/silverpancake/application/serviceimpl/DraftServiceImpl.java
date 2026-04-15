package silverpancake.application.serviceimpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import silverpancake.application.mapper.DraftMapper;
import silverpancake.application.mapper.TeamMapper;
import silverpancake.application.model.draft.DraftModel;
import silverpancake.application.repository.*;
import silverpancake.application.service.DraftService;
import silverpancake.application.util.DraftPickTurnShuffler;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.draft.Draft;
import silverpancake.domain.entity.draft.DraftPickTurn;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.team.Team;
import silverpancake.domain.entity.user.User;
import silverpancake.domain.entity.usercourse.UserCourse;
import silverpancake.domain.entity.userteam.UserTeam;
import silverpancake.presentation.websocket.WebSocketSender;
import silverpancake.presentation.websocket.model.draft.OrderOfSelectionChangedModel;
import silverpancake.presentation.websocket.model.draft.TeamStructureChanged;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static silverpancake.domain.entity.user.UserCourseRole.STUDENT;

@Service
@RequiredArgsConstructor
public class DraftServiceImpl implements DraftService {

    private final TransactionTemplate transactionTemplate;

    private final DraftRepository draftRepository;

    private final TeamRepository teamRepository;

    private final UserTeamRepository userTeamRepository;

    private final WebSocketSender webSocketSender;

    private final DraftPickTurnRepository draftPickTurnRepository;

    private final DraftPickTurnShuffler draftPickTurnShuffler;

    private final ExceptionUtility exceptionUtility;

    @Override
    public DraftModel getDraft(UUID userId, UUID draftId) {
        if (!canUserObserveDraft(userId, draftId)) {
            throw exceptionUtility.draftIsNotObservableByUserException();
        }

        Draft draft = draftRepository.findById(draftId).orElseThrow(exceptionUtility::draftNotFoundException);
        draft.getDraftPickTurns().sort(Comparator.comparing(DraftPickTurn::getOrder));

        return DraftMapper.toModel(draft);
    }

    @Override
    public void updateNextSelectingCaptain(Draft draft) {
        List<DraftPickTurn> draftPickTurns = draft.getDraftPickTurns();
        draftPickTurns.sort(Comparator.comparing(DraftPickTurn::getOrder));
        DraftPickTurn draftPickTurnToRemove = draftPickTurns.removeFirst();

        if (!draftPickTurns.isEmpty()) {
            draft.setCurrentSelectingCaptain(draftPickTurns.getFirst().getUser());

        } else {
            draft.setCurrentSelectingCaptain(null);
        }

        draftPickTurnRepository.delete(draftPickTurnToRemove);
        draftRepository.saveAndFlush(draft);

        if (!draftPickTurns.isEmpty()) {
            Team captainTeam = draft.getTeams()
                    .stream()
                    .filter(t -> Objects.equals(t.getCaptain(), draft.getCurrentSelectingCaptain()))
                    .findFirst()
                    .get();

            startPickTimerForDraft(draft, draftPickTurns.getFirst().getId(), captainTeam.getId());

            var message = new OrderOfSelectionChangedModel()
                    .setDraftPickTurnModels(draftPickTurns
                            .stream()
                            .map(DraftMapper::toModel)
                            .toList());
            webSocketSender.sendOrderOfSelectionChangedMessage(
                    message,
                    draft.getId());
        } else {
            endDraft(draft);
        }
    }

    private void startPickTimerForDraft(Draft draft, UUID draftPickTurnId, UUID teamId) {
        draft.setLastPickTime(LocalDateTime.now());
        draftRepository.saveAndFlush(draft);

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                transactionTemplate.execute(status -> {
                    var currentDraftState = draftRepository.findById(draft.getId());
                    if (currentDraftState.isEmpty() || currentDraftState.get().getIsEnded() == true) {
                        return null;
                    }
                    var draftNewState = currentDraftState.get();
                    var nextDraftPickTurn = draftNewState.getDraftPickTurns().stream().min(Comparator.comparing(DraftPickTurn::getOrder)).get();
                    if (draftPickTurnId.equals(nextDraftPickTurn.getId())) {
                        draftPickTurnRepository.deleteById(draftPickTurnId);

                        var shuffledFreeStudents = getStudentsToDraft(draftNewState, new Integer[1]);
                        var randomUserCourse = shuffledFreeStudents.get(ThreadLocalRandom.current().nextInt(shuffledFreeStudents.size()));

                        var team = teamRepository.findById(teamId).get();
                        var currentTeamMembers = team.getTeamMembers();
                        var userTeam = new UserTeam()
                                .setUser(randomUserCourse.getUser())
                                .setTeam(team)
                                .setCreatedAt(LocalDateTime.now());
                        currentTeamMembers.add(userTeam);
                        userTeamRepository.saveAndFlush(userTeam);

                        webSocketSender.sendAutoSelectionPerformedMessage(
                                draftNewState.getCurrentSelectingCaptain().getId(),
                                draft.getId()
                        );
                        webSocketSender.sendTeamStructureChangedMessage(
                                new TeamStructureChanged()
                                        .setChangedTeam(TeamMapper.toModel(team, null)),
                                draftNewState.getId());

                        updateNextSelectingCaptain(draftNewState);
                    }
                    return null;
                });
            }
        }, 60 * 1000);
    }

    @Override
    public void createDraft(List<Team> teams, Task task) {
        Draft draft = new Draft()
                .setTask(task)
                .setTeams(teams)
                .setIsEnded(false)
                .setIsStarted(false);

        teams.forEach(team -> team.setDraft(draft));
        task.setDraft(draft);

        draftRepository.saveAndFlush(draft);
    }

    @Override
    @Transactional
    public void createOrReloadDraftPickTurns(Task task, boolean isCreating) {
        Draft draft = draftRepository.findByTaskId(task.getId())
                .orElseThrow(exceptionUtility::draftNotFoundException);

        if (draft.getIsEnded()) {
            throw exceptionUtility.draftAlreadyEndedException();
        }

        List<User> captains = draft.getTeams()
                .stream()
                .map(Team::getCaptain)
                .toList();
        Integer[] countOfSelectedStudents = new Integer[1];
        countOfSelectedStudents[0] = 0;
        List<UserCourse> freeStudents = getStudentsToDraft(draft, countOfSelectedStudents);

        if (captains.size() != draft.getTeams().size()) {
            throw exceptionUtility.teamsHaveNotEnoughCaptainsException();
        }

        List<DraftPickTurn> draftPickTurns;
        if (isCreating) {
            List<User> captainsPickOrder = draftPickTurnShuffler.getShuffledCaptainsByStudents(
                    captains,
                    freeStudents.size());
            draftPickTurns = mapCaptainsPickOrderToDraftPickTurn(captainsPickOrder, draft);

            draftPickTurnRepository.deleteByDraftId(draft.getId());
            draftPickTurnRepository.saveAllAndFlush(draftPickTurns);
        } else {
            if (!draft.getIsEnded()) {
                draftPickTurns = draftPickTurnShuffler.continueCaptainsByStudents(
                        captains,
                        draft.getDraftPickTurns(),
                        freeStudents.size(),
                        countOfSelectedStudents[0],
                        draft);
                draftPickTurnRepository.saveAllAndFlush(draftPickTurns);

                var message = new OrderOfSelectionChangedModel()
                        .setDraftPickTurnModels(draftPickTurns
                                .stream()
                                .map(DraftMapper::toModel)
                                .toList());
                webSocketSender.sendOrderOfSelectionChangedMessage(
                        message,
                        draft.getId());
            }
        }
    }

    private List<UserCourse> getStudentsToDraft(Draft draft, Integer[] countOfSelectedStudents) {
        List<UUID> alreadySelectedStudentIds = new ArrayList<>();
        draft.getTeams().forEach(team -> {
                    alreadySelectedStudentIds.addAll(team
                            .getTeamMembers()
                            .stream()
                            .map(t -> t.getUser().getId())
                            .toList());
                    if (team.getCaptain() != null) {
                        alreadySelectedStudentIds.add(team.getCaptain().getId());
                    }
                }
        );
        countOfSelectedStudents[0] = alreadySelectedStudentIds.size();
        return draft
                .getTask()
                .getCourse()
                .getCourseUsers()
                .stream()
                .filter(u -> !alreadySelectedStudentIds.contains(u.getUser().getId())
                            && STUDENT.equals(u.getUserRole()))
                .toList();
    }

    private List<DraftPickTurn> mapCaptainsPickOrderToDraftPickTurn(List<User> captainsPickOrder, Draft draft) {
        List<DraftPickTurn> draftPickTurns = new ArrayList<>();
        for (int i = 0; i < captainsPickOrder.size(); i++) {
            draftPickTurns.add(
                    new DraftPickTurn()
                            .setDraft(draft)
                            .setOrder(i)
                            .setUser(captainsPickOrder.get(i)));
        }
        return draftPickTurns;
    }

    @Override
    public void startDraft(Draft draft) {
        draft.setIsStarted(true);
        draft.getDraftPickTurns().sort(Comparator.comparing(DraftPickTurn::getOrder));
        draft.setCurrentSelectingCaptain(draft.getDraftPickTurns().getFirst().getUser());

        DraftModel draftModel = DraftMapper.toModel(draft);

        draftRepository.saveAndFlush(draft);

        Team captainTeam = draft.getTeams()
                .stream()
                .filter(t -> Objects.equals(t.getCaptain(), draft.getCurrentSelectingCaptain()))
                .findFirst()
                .get();

        startPickTimerForDraft(draft, draft.getDraftPickTurns().getFirst().getId(), captainTeam.getId());

        webSocketSender.sendDraftStartedMessage(draftModel);
    }

    @Override
    public void endDraft(Draft draft) {
        draft.setIsEnded(true);
        draft.setLastPickTime(null);
        draftRepository.saveAndFlush(draft);

        webSocketSender.sendDraftEndedMessage(draft.getId());
    }

    @Override
    public List<Draft> getCurrentDraftsByCourse(Course course) {
        return draftRepository.findNotEndedByCourse(course.getId());
    }

    @Override
    public boolean canUserObserveDraft(UUID userId, UUID draftId) {
        if (userId == null || draftId == null) {
            return false;
        }
        return draftRepository.isDraftCourseContainsUser(draftId, userId);
    }
}
