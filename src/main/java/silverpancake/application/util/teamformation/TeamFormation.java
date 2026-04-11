package silverpancake.application.util.teamformation;

import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.team.Team;

import java.util.List;

public interface TeamFormation {
    void onTaskCreation(Task task, List<Team> teams);

    void onLastCaptainSelection(Task task);
}
