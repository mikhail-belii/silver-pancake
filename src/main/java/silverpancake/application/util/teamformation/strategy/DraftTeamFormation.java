package silverpancake.application.util.teamformation.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import silverpancake.application.mapper.TeamMapper;
import silverpancake.application.repository.DraftPickTurnRepository;
import silverpancake.application.repository.DraftRepository;
import silverpancake.application.repository.TeamRepository;
import silverpancake.application.service.DraftService;
import silverpancake.application.util.teamformation.TeamFormation;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.draft.Draft;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.team.Team;
import silverpancake.domain.entity.usercourse.UserCourse;
import silverpancake.presentation.websocket.WebSocketSender;
import silverpancake.presentation.websocket.model.draft.TeamStructureChanged;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DraftTeamFormation implements TeamFormation {

    private final DraftRepository draftRepository;

    private final TeamRepository teamRepository;

    private final DraftService draftService;

    private final WebSocketSender webSocketSender;

    @Override
    public void onTaskCreation(Task task, List<Team> teams) {
        draftService.createDraft(teams, task);
    }

    public void notifyOnNewUserJoinedCourse(Course course) {
        List<Draft> courseDrafts = draftService.getCurrentDraftsByCourse(course);
        courseDrafts.forEach(draft -> {
            draftService.createOrReloadDraftPickTurns(draft.getTask(), false);
        });
    }

    public void notifyOnStudentPromotedToTeacher(Course course, UserCourse userCourse) {
        for (Task task : course.getTasks()) {
            Draft draft = task.getDraft();
            Optional<Team> teamToChange = task.getTeams()
                    .stream()
                    .filter(t -> t.getTeamMembers()
                            .stream()
                            .anyMatch(u -> Objects.equals(u.getUser(), userCourse.getUser()))
                    ).findFirst();
            //Добавить проверку капитана

            if (teamToChange.isPresent()) {
                teamToChange.get().getTeamMembers().removeIf(u -> Objects.equals(u.getUser(), userCourse.getUser()));
                teamRepository.saveAndFlush(teamToChange.get());

                if (draft != null && draft.getIsEnded() == false) {
                    TeamStructureChanged teamStructureChanged = new TeamStructureChanged()
                            .setChangedTeam(TeamMapper.toModel(teamToChange.get()));

                    webSocketSender.sendTeamStructureChangedMessage(teamStructureChanged, draft.getId());
                }

            }
        }
    }

    public void notifyOnStudentJoinedTeam(Team team) {
        Draft draft = team.getDraft();
        if (draft != null && draft.getIsEnded() == false) {
            TeamStructureChanged teamStructureChanged = new TeamStructureChanged()
                    .setChangedTeam(TeamMapper.toModel(team));

            webSocketSender.sendTeamStructureChangedMessage(teamStructureChanged, draft.getId());
            draftService.updateNextSelectingCaptain(draft);
        }
    }

    public void notifyOnLastStudentJoinedTeam(Team team) {
        Draft draft = team.getDraft();
        // перенести в draft service
        if (draft != null && draft.getIsEnded() == false) {
            team.getDraft().setIsEnded(true);
            draftRepository.saveAndFlush(draft);
            webSocketSender.sendDraftEndedMessage(draft.getId());
        }
    }

    @Override
    public void onLastCaptainSelection(Task task) {
        draftService.createOrReloadDraftPickTurns(task, true);
        draftService.startDraft(task.getDraft());
    }
}
