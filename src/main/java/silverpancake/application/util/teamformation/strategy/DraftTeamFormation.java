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
import silverpancake.domain.entity.user.User;
import silverpancake.domain.entity.usercourse.UserCourse;
import silverpancake.presentation.websocket.WebSocketSender;
import silverpancake.presentation.websocket.model.draft.TeamStructureChanged;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static silverpancake.domain.entity.task.TeamFormationType.DRAFT;

@Component
@RequiredArgsConstructor
public class DraftTeamFormation implements TeamFormation {

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

    public void notifyOnStudentJoinedTeam(Team team) {
        Draft draft = team.getDraft();
        if (draft.getIsEnded() == false) {
            TeamStructureChanged teamStructureChanged = new TeamStructureChanged()
                    .setChangedTeam(TeamMapper.toModel(team));

            webSocketSender.sendTeamStructureChangedMessage(teamStructureChanged, draft.getId());
            draftService.updateNextSelectingCaptain(draft);
        }
    }

    public void notifyOnStudentRemovedFromTeam(Team team) {
        if (team.getDraft().getIsEnded() == false) {
            TeamStructureChanged teamStructureChanged = new TeamStructureChanged()
                    .setChangedTeam(TeamMapper.toModel(team));

            webSocketSender.sendTeamStructureChangedMessage(teamStructureChanged, team.getDraft().getId());
            draftService.createOrReloadDraftPickTurns(team.getTask(), false);
        }
    }

    public void notifyOnLastStudentJoinedTeam(Draft draft) {
        if (draft.getIsEnded() == false) {
            draftService.endDraft(draft);
        }
    }

    @Override
    public void onLastCaptainSelection(Task task) {
        draftService.createOrReloadDraftPickTurns(task, true);
        draftService.startDraft(task.getDraft());
    }
}
