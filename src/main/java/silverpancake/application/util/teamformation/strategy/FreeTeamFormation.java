package silverpancake.application.util.teamformation.strategy;

import org.springframework.stereotype.Component;
import silverpancake.application.util.teamformation.TeamFormation;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.draft.Draft;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.team.Team;

import java.util.List;

@Component
public class FreeTeamFormation implements TeamFormation {
    @Override
    public void onTaskCreation(Task task, List<Team> teams) {

    }

    @Override
    public void onLastCaptainSelection(Task task) {

    }
}
