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
    private final UserTeamRepository userTeamRepository;

    @Override
    public void formTeams(Task task, List<Team> teams) {
        if (teams.isEmpty() || task.getCourse() == null || task.getCourse().getCourseUsers() == null) {
            return;
        }

        var shuffledStudents = new ArrayList<>(task.getCourse().getCourseUsers()
                .stream()
                .filter(userCourse -> userCourse.getUserRole() == UserCourseRole.STUDENT)
                .map(UserCourse::getUser)
                .toList());

        if (shuffledStudents.isEmpty()) {
            return;
        }

        Collections.shuffle(shuffledStudents);

        var userTeams = new ArrayList<UserTeam>();
        for (int i = 0; i < shuffledStudents.size(); i++) {
            var userTeam = new UserTeam()
                    .setUser(shuffledStudents.get(i))
                    .setTeam(teams.get(i % teams.size()))
                    .setCreatedAt(LocalDateTime.now());
            userTeams.add(userTeam);
        }

        userTeamRepository.saveAll(userTeams);
        userTeamRepository.flush();
    }
}
