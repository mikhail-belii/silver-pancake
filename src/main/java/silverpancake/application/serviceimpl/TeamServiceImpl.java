package silverpancake.application.serviceimpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.TeamMapper;
import silverpancake.application.mapper.UserCourseMapper;
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
import silverpancake.domain.entity.user.UserCourseRole;
import silverpancake.domain.entity.userteam.UserTeam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final TeamRepository teamRepository;
    private final UserTeamRepository userTeamRepository;
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

        return new TeamShortListModel(teams.stream().map(TeamMapper::toShortModel).toList());
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

        return TeamMapper.toModel(team);
    }

    @Override
    @Transactional
    public TeamModel assignTeamCaptain(UUID requestingUserId, UUID teamId, UUID studentId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var team = teamRepository.findById(teamId)
                .orElseThrow(exceptionUtility::teamNotFoundException);
        var task = taskRepository.findById(team.getTask().getId())
                .orElseThrow(exceptionUtility::taskNotFoundException);
        var userCourse = userCourseRepository.findByUserAndCourse(user.getId(), task.getCourse().getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        validateTaskDeadlineNotExpired(task);

        if (userCourse.getUserRole().equals(UserCourseRole.STUDENT)) {
            throw exceptionUtility.securityException();
        }

        var student = userRepository.findById(studentId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var studentUserCourse = userCourseRepository.findByUserAndCourse(student.getId(), task.getCourse().getId())
                .orElseThrow(exceptionUtility::targetUserNotCourseMemberException);

        if (!studentUserCourse.getUserRole().equals(UserCourseRole.STUDENT)) {
            throw exceptionUtility.onlyStudentCanBeTeamMemberException();
        }

        if (team.getCaptain() != null) {
            if (team.getCaptain().equals(student)) {
                throw exceptionUtility.studentAlreadyCaptainException();
            }
            throw exceptionUtility.teamAlreadyHasCaptainException();
        }

        var studentUserTeam = userTeamRepository.findByUserIdAndTeamTaskId(student.getId(), task.getId());
        if (studentUserTeam.isPresent()) {
            var userTeam = studentUserTeam.get();
            if (!userTeam.getTeam().getId().equals(teamId)) {
                throw exceptionUtility.studentAlreadyInAnotherTeamException();
            }

            userTeamRepository.delete(userTeam);
            if (team.getTeamMembers() != null) {
                team.getTeamMembers().removeIf(teamMember -> teamMember.getId().equals(userTeam.getId()));
            }
        }

        team.setCaptain(student);
        teamRepository.save(team);

        return TeamMapper.toModel(team);
    }

    @Override
    public UserCourseListModel getFreeStudentsForTask(UUID requestingUserId, UUID taskId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var task = taskRepository.findById(taskId)
                .orElseThrow(exceptionUtility::taskNotFoundException);
        var userCourse = userCourseRepository.findByUserAndCourse(user.getId(), task.getCourse().getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        if (userCourse.getUserRole().equals(UserCourseRole.STUDENT)) {
            throw exceptionUtility.securityException();
        }

        var busyStudentIds = new HashSet<UUID>();
        var teams = teamRepository.findTeamsByTask(task);
        for (var team : teams) {
            if (team.getCaptain() != null) {
                busyStudentIds.add(team.getCaptain().getId());
            }

            if (team.getTeamMembers() != null) {
                team.getTeamMembers().forEach(teamMember -> busyStudentIds.add(teamMember.getUser().getId()));
            }
        }

        var freeStudents = task.getCourse().getCourseUsers()
                .stream()
                .filter(courseUser -> courseUser.getUserRole().equals(UserCourseRole.STUDENT))
                .filter(courseUser -> !busyStudentIds.contains(courseUser.getUser().getId()))
                .map(UserCourseMapper::toModel)
                .toList();

        return new UserCourseListModel(freeStudents);
    }

    @Override
    @Transactional
    public TeamModel joinTeam(UUID requestingUserId, UUID teamId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var team = teamRepository.findById(teamId)
                .orElseThrow(exceptionUtility::teamNotFoundException);
        var task = taskRepository.findById(team.getTask().getId())
                .orElseThrow(exceptionUtility::taskNotFoundException);
        var userCourse = userCourseRepository.findByUserAndCourse(user.getId(), task.getCourse().getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        validateTaskDeadlineNotExpired(task);

        if (!userCourse.getUserRole().equals(UserCourseRole.STUDENT)) {
            throw exceptionUtility.securityException();
        }

        if (!task.getTeamFormationType().equals(TeamFormationType.FREE)) {
            throw exceptionUtility.teamJoiningAvailableOnlyForFreeFormationException();
        }

        var studentCaptainTeam = teamRepository.findByCaptainIdAndTaskId(user.getId(), task.getId());
        if (studentCaptainTeam.isPresent()) {
            if (studentCaptainTeam.get().getId().equals(teamId)) {
                throw exceptionUtility.studentAlreadyCaptainException();
            }

            throw exceptionUtility.studentAlreadyInAnotherTeamException();
        }

        var studentUserTeam = userTeamRepository.findByUserIdAndTeamTaskId(user.getId(), task.getId());
        if (studentUserTeam.isPresent()) {
            if (studentUserTeam.get().getTeam().getId().equals(teamId)) {
                throw exceptionUtility.studentAlreadyInThisTeamException();
            }

            throw exceptionUtility.studentAlreadyInAnotherTeamException();
        }

        var userTeam = new UserTeam()
                .setUser(user)
                .setTeam(team)
                .setCreatedAt(LocalDateTime.now());

        userTeam = userTeamRepository.save(userTeam);

        if (team.getTeamMembers() == null) {
            team.setTeamMembers(new ArrayList<>());
        }
        team.getTeamMembers().add(userTeam);

        return TeamMapper.toModel(team);
    }

    @Override
    @Transactional
    public TeamModel leaveTeam(UUID requestingUserId, UUID teamId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var team = teamRepository.findById(teamId)
                .orElseThrow(exceptionUtility::teamNotFoundException);
        var task = taskRepository.findById(team.getTask().getId())
                .orElseThrow(exceptionUtility::taskNotFoundException);
        var userCourse = userCourseRepository.findByUserAndCourse(user.getId(), task.getCourse().getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        validateTaskDeadlineNotExpired(task);

        if (!userCourse.getUserRole().equals(UserCourseRole.STUDENT)) {
            throw exceptionUtility.securityException();
        }

        if (!task.getTeamFormationType().equals(TeamFormationType.FREE)) {
            throw exceptionUtility.teamJoiningAvailableOnlyForFreeFormationException();
        }

        if (team.getCaptain() != null && team.getCaptain().getId().equals(user.getId())) {
            team.setCaptain(null);
            teamRepository.save(team);

            return TeamMapper.toModel(team);
        }

        var studentUserTeam = userTeamRepository.findByUserIdAndTeamTaskId(user.getId(), task.getId());
        if (studentUserTeam.isEmpty()) {
            throw exceptionUtility.studentNotInThisTeamException();
        }

        var userTeam = studentUserTeam.get();
        if (!userTeam.getTeam().getId().equals(teamId)) {
            throw exceptionUtility.studentNotInThisTeamException();
        }

        userTeamRepository.delete(userTeam);
        if (team.getTeamMembers() != null) {
            team.getTeamMembers().removeIf(teamMember -> teamMember.getId().equals(userTeam.getId()));
        }

        return TeamMapper.toModel(team);
    }

    @Override
    @Transactional
    public TeamModel addTeamMember(UUID requestingUserId, UUID teamId, UUID studentId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var team = teamRepository.findById(teamId)
                .orElseThrow(exceptionUtility::teamNotFoundException);
        var task = taskRepository.findById(team.getTask().getId())
                .orElseThrow(exceptionUtility::taskNotFoundException);
        var userCourse = userCourseRepository.findByUserAndCourse(user.getId(), task.getCourse().getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        validateTaskDeadlineNotExpired(task);

        if (userCourse.getUserRole().equals(UserCourseRole.STUDENT)) {
            throw exceptionUtility.securityException();
        }

        var student = userRepository.findById(studentId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var studentUserCourse = userCourseRepository.findByUserAndCourse(student.getId(), task.getCourse().getId())
                .orElseThrow(exceptionUtility::targetUserNotCourseMemberException);

        if (!studentUserCourse.getUserRole().equals(UserCourseRole.STUDENT)) {
            throw exceptionUtility.onlyStudentCanBeTeamMemberException();
        }

        var studentCaptainTeam = teamRepository.findByCaptainIdAndTaskId(student.getId(), task.getId());
        if (studentCaptainTeam.isPresent()) {
            if (studentCaptainTeam.get().getId().equals(teamId)) {
                throw exceptionUtility.studentAlreadyCaptainException();
            }

            throw exceptionUtility.studentAlreadyInAnotherTeamException();
        }

        var studentUserTeam = userTeamRepository.findByUserIdAndTeamTaskId(student.getId(), task.getId());
        if (studentUserTeam.isPresent()) {
            if (studentUserTeam.get().getTeam().getId().equals(teamId)) {
                throw exceptionUtility.studentAlreadyInThisTeamException();
            }

            throw exceptionUtility.studentAlreadyInAnotherTeamException();
        }

        var userTeam = new UserTeam()
                .setUser(student)
                .setTeam(team)
                .setCreatedAt(LocalDateTime.now());

        userTeam = userTeamRepository.save(userTeam);

        if (team.getTeamMembers() == null) {
            team.setTeamMembers(new ArrayList<>());
        }
        team.getTeamMembers().add(userTeam);

        return TeamMapper.toModel(team);
    }

    @Override
    @Transactional
    public TeamModel removeTeamMember(UUID requestingUserId, UUID teamId, UUID teamMemberId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var team = teamRepository.findById(teamId)
                .orElseThrow(exceptionUtility::teamNotFoundException);
        var task = taskRepository.findById(team.getTask().getId())
                .orElseThrow(exceptionUtility::taskNotFoundException);
        var userCourse = userCourseRepository.findByUserAndCourse(user.getId(), task.getCourse().getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        validateTaskDeadlineNotExpired(task);

        if (userCourse.getUserRole().equals(UserCourseRole.STUDENT)) {
            throw exceptionUtility.securityException();
        }

        var teamMember = userRepository.findById(teamMemberId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var teamMemberUserCourse = userCourseRepository.findByUserAndCourse(teamMember.getId(), task.getCourse().getId())
                .orElseThrow(exceptionUtility::targetUserNotCourseMemberException);

        if (!teamMemberUserCourse.getUserRole().equals(UserCourseRole.STUDENT)) {
            throw exceptionUtility.onlyStudentCanBeTeamMemberException();
        }

        if (team.getCaptain() != null && team.getCaptain().getId().equals(teamMemberId)) {
            team.setCaptain(null);
            teamRepository.save(team);

            return TeamMapper.toModel(team);
        }

        var studentUserTeam = userTeamRepository.findByUserIdAndTeamTaskId(teamMember.getId(), task.getId());
        if (studentUserTeam.isEmpty()) {
            throw exceptionUtility.studentNotInThisTeamException();
        }

        var userTeam = studentUserTeam.get();
        if (!userTeam.getTeam().getId().equals(teamId)) {
            throw exceptionUtility.studentNotInThisTeamException();
        }

        userTeamRepository.delete(userTeam);
        if (team.getTeamMembers() != null) {
            team.getTeamMembers().removeIf(teamMemberEntity -> teamMemberEntity.getId().equals(userTeam.getId()));
        }

        return TeamMapper.toModel(team);
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

    private void validateTaskDeadlineNotExpired(Task task) {
        if (task.getDeadline() != null && LocalDateTime.now().isAfter(task.getDeadline())) {
            throw exceptionUtility.taskDeadlineExpiredException();
        }
    }
}
