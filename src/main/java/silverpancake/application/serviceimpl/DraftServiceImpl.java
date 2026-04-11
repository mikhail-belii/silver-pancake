package silverpancake.application.serviceimpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.DraftMapper;
import silverpancake.application.mapper.TeamMapper;
import silverpancake.application.model.draft.DraftModel;
import silverpancake.application.model.draft.DraftPickTurnModel;
import silverpancake.application.model.team.TeamModel;
import silverpancake.application.repository.*;
import silverpancake.application.service.DraftService;
import silverpancake.application.service.TeamService;
import silverpancake.application.util.DraftPickTurnShuffler;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.draft.Draft;
import silverpancake.domain.entity.draft.DraftPickTurn;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.team.Team;
import silverpancake.domain.entity.user.User;
import silverpancake.domain.entity.usercourse.UserCourse;
import silverpancake.presentation.websocket.WebSocketSender;
import silverpancake.presentation.websocket.model.draft.OrderOfSelectionChangedModel;

import java.util.*;

import static silverpancake.domain.entity.user.UserCourseRole.STUDENT;

@Service
@RequiredArgsConstructor
public class DraftServiceImpl implements DraftService {

    private final DraftRepository draftRepository;

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
        List<UserCourse> freeStudents = getStudentsToDraft(draft);

        if (captains.size() != draft.getTeams().size()) {
            throw exceptionUtility.teamsHaveNotEnoughCaptainsException();
        }

        List<User> captainsPickOrder = draftPickTurnShuffler.getShuffledCaptainsByStudents(
                captains,
                freeStudents.size());
        List<DraftPickTurn> draftPickTurns = mapCaptainsPickOrderToDraftPickTurn(captainsPickOrder, draft);

        draftPickTurnRepository.deleteByDraftId(draft.getId());
        draftPickTurnRepository.saveAllAndFlush(draftPickTurns);

        if (!isCreating) {
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

    private List<UserCourse> getStudentsToDraft(Draft draft) {
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

        DraftModel draftModel = DraftMapper.toModel(draft);

        draftRepository.saveAndFlush(draft);

        webSocketSender.sendDraftStartedMessage(draftModel);
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
