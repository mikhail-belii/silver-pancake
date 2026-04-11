package silverpancake.application.util.teamformation.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import silverpancake.application.repository.DraftPickTurnRepository;
import silverpancake.application.repository.DraftRepository;
import silverpancake.application.service.DraftService;
import silverpancake.application.util.teamformation.TeamFormation;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.draft.Draft;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.team.Team;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DraftTeamFormation implements TeamFormation {

    private final DraftService draftService;

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

    @Override
    public void onLastCaptainSelection(Task task) {
        draftService.createOrReloadDraftPickTurns(task, true);
        draftService.startDraft(task.getDraft());
    }
}
