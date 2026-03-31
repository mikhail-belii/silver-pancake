package silverpancake.application.util.teamformation.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import silverpancake.application.repository.UserTeamRepository;
import silverpancake.application.util.teamformation.TeamFormation;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.team.Team;
import silverpancake.domain.entity.user.UserCourseRole;
import silverpancake.domain.entity.usercourse.UserCourse;
import silverpancake.domain.entity.userteam.UserTeam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RandomTeamFormation implements TeamFormation {
    @Override
    public void formTeams(Task task, List<Team> teams) {
    }
}
