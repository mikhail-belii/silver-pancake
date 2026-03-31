package silverpancake.application.serviceimpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.TeamMapper;
import silverpancake.application.model.course.UserCourseListModel;
import silverpancake.application.model.team.TeamModel;
import silverpancake.application.model.team.TeamShortListModel;
import silverpancake.application.repository.*;
import silverpancake.application.service.TeamService;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.application.util.TeamProperties;
import silverpancake.application.util.teamformation.TeamFormationFactory;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.task.TeamFormationType;
import silverpancake.domain.entity.team.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final TeamRepository teamRepository;
    private final TeamProperties teamProperties;
    private final TeamFormationFactory teamFormationFactory;
    private final ExceptionUtility exceptionUtility;

    @Override
    @Transactional
    public void createTeamsOnTaskCreated(Task task, Integer teamsAmount, TeamFormationType teamFormationType) {
        var availableTeamNames = getShuffledTeamNames();
        var teams = new ArrayList<Team>();

        for (int i = 0; i < teamsAmount; i++) {
            var team = new Team()
                    .setTask(task)
                    .setName(resolveTeamName(availableTeamNames, i));

            teams.add(team);
            teamRepository.save(team);
        }

        teamRepository.flush();
        teamFormationFactory.createTeamFormation(teamFormationType).formTeams(task, teams);
    }

    @Override
    public TeamShortListModel getTeams(UUID requestingUserId, UUID taskId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var task = taskRepository.findById(taskId)
                .orElseThrow(exceptionUtility::taskNotFoundException);
        userCourseRepository.findByUserAndCourse(user.getId(), task.getCourse().getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);
        var teams = teamRepository.findTeamsByTask(task);

        return new TeamShortListModel(teams.stream().map(TeamMapper::toTeamShortModel).toList());
    }

    @Override
    public TeamModel getTeam(UUID requestingUserId, UUID teamId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var team = teamRepository.findById(teamId)
                .orElseThrow(exceptionUtility::teamNotFoundException);
        var task = taskRepository.findById(team.getTask().getId())
                .orElseThrow(exceptionUtility::taskNotFoundException);
        userCourseRepository.findByUserAndCourse(user.getId(), task.getCourse().getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        return TeamMapper.toTeamModel(team);
    }

    @Override
    public void assignTeamCaptain(UUID requestingUserId, UUID teamId, UUID studentId) {

    }

    @Override
    public UserCourseListModel getFreeStudentsForTask(UUID requestingUserId, UUID taskId) {
        return null;
    }

    @Override
    public void addTeamMember(UUID requestingUserId, UUID teamId, UUID studentId) {

    }

    private ArrayList<String> getShuffledTeamNames() {
        var teamNames = teamProperties.getTeamNames();
        if (teamNames == null || teamNames.isEmpty()) {
            return new ArrayList<>();
        }

        var shuffledTeamNames = new ArrayList<>(teamNames);
        Collections.shuffle(shuffledTeamNames);
        return shuffledTeamNames;
    }

    private String resolveTeamName(ArrayList<String> availableTeamNames, int index) {
        if (index < availableTeamNames.size()) {
            return availableTeamNames.get(index);
        }

        return "Team " + (index + 1);
    }
}
