package silverpancake.application.serviceimpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.CourseMapper;
import silverpancake.application.mapper.TeamMapper;
import silverpancake.application.mapper.UserCourseMapper;
import silverpancake.application.model.course.*;
import silverpancake.application.repository.CourseRepository;
import silverpancake.application.repository.UserCourseRepository;
import silverpancake.application.repository.UserRepository;
import silverpancake.application.service.CourseService;
import silverpancake.application.service.TeamService;
import silverpancake.application.util.CourseCodeGenerator;
import silverpancake.application.util.CourseUtility;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.application.util.teamformation.TeamFormationFactory;
import silverpancake.application.util.teamformation.strategy.DraftTeamFormation;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.draft.Draft;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.team.Team;
import silverpancake.domain.entity.user.User;
import silverpancake.domain.entity.user.UserCourseRole;
import silverpancake.domain.entity.usercourse.UserCourse;
import silverpancake.presentation.websocket.model.draft.TeamStructureChanged;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static silverpancake.domain.entity.task.TeamFormationType.DRAFT;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl  implements CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;
    private final CourseCodeGenerator courseCodeGenerator;
    private final ExceptionUtility exceptionUtility;
    private final CourseMapper courseMapper;
    private final DraftTeamFormation draftTeamFormation;
    private final TeamService teamService;

    @Override
    @Transactional
    public CourseModel createCourse(UUID requestingUserId, CourseCreateModel courseCreateModel) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var course = createCourseFromCreateModel(courseCreateModel);
        var userCourse = createUserCourseOnCourseCreation(course, user);

        courseRepository.save(course);
        userCourseRepository.save(userCourse);

        return courseMapper.toModel(course, userCourse.getUserRole());
    }

    @Override
    public CourseModel editCourse(UUID requestingUserId, UUID courseId, CourseEditModel courseEditModel) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var course = courseRepository.findById(courseId)
                .orElseThrow(exceptionUtility::courseNotFoundException);
        var userCourse = userCourseRepository.findByUserAndCourse(user.getId(), course.getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        if (!CourseUtility.isCourseAvailableForEditing(course, user)) {
            throw exceptionUtility.securityException();
        }

        course.setName(courseEditModel.getName())
                .setDescription(courseEditModel.getDescription());

        courseRepository.save(course);

        return courseMapper.toModel(course, userCourse.getUserRole());
    }

    @Override
    public UserCourseListModel getCourseUsers(UUID requestingUserId, UUID courseId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var course = courseRepository.findById(courseId)
                .orElseThrow(exceptionUtility::courseNotFoundException);

        if (CourseUtility.getUserCourse(course, user).isEmpty()) {
            throw exceptionUtility.securityException();
        }

        return new UserCourseListModel(course.getCourseUsers()
                .stream()
                .map(UserCourseMapper::toModel)
                .toList());
    }

    @Override
    public CourseModel getConcreteCourse(UUID requestingUserId, UUID courseId) {
        var user =  userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var course = courseRepository.findById(courseId)
                .orElseThrow(exceptionUtility::courseNotFoundException);
        var userCourse = userCourseRepository.findByUserAndCourse(user.getId(), course.getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        return courseMapper.toModel(course, userCourse.getUserRole());
    }

    @Override
    public CourseShortListModel getUserCourses(UUID requestingUserId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);

        return new CourseShortListModel(user.getUserCourses()
                .stream()
                .map(uc -> courseMapper.toShortModel(uc.getCourse()))
                .toList());
    }

    @Override
    public CourseModel joinCourseByCode(UUID requestingUserId, String code) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var course = courseRepository.findByJoinCode(code)
                .orElseThrow(exceptionUtility::courseNotFoundException);

        if (CourseUtility.getUserCourse(course, user).isPresent()) {
            throw exceptionUtility.userAlreadyCourseMemberException();
        }

        var userCourse = createUserCourseOnCourseJoin(course, user);

        course.getCourseUsers().add(userCourse);
        userCourseRepository.saveAndFlush(userCourse);

        draftTeamFormation.notifyOnNewUserJoinedCourse(course);

        return courseMapper.toModel(course, userCourse.getUserRole());
    }

    @Override
    public void changeUserRoleOnCourse(UUID requestingUserId, UUID courseId, UUID userId, UserCourseRole newUserRole) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var userToChange = userRepository.findById(userId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var course = courseRepository.findById(courseId)
                .orElseThrow(exceptionUtility::courseNotFoundException);

        var userCourse = CourseUtility.getUserCourse(course, userToChange)
                .orElseThrow(exceptionUtility::targetUserNotCourseMemberException);

        if (!CourseUtility.isUserAvailableToChangeOtherUserRoleOnCourse(course, userToChange, newUserRole, user)) {
            throw exceptionUtility.securityException();
        }

        if (Objects.equals(userCourse.getUserRole(), UserCourseRole.STUDENT)
            && Objects.equals(newUserRole, UserCourseRole.TEACHER)) {
            teamService.removeUserFromCourseTeams(course, userToChange);
        }

        userCourse.setUserRole(newUserRole);
        userCourseRepository.save(userCourse);
    }

    @Override
    public void removeUserFromCourse(UUID requestingUserId, UUID courseId, UUID userId) {

    }

    private Course createCourseFromCreateModel(CourseCreateModel courseCreateModel) {
        return new Course()
                .setName(courseCreateModel.getName())
                .setDescription(courseCreateModel.getDescription())
                .setJoinCode(courseCodeGenerator.generateNewCode())
                .setCreatedAt(LocalDateTime.now());
    }

    private UserCourse createUserCourseOnCourseCreation(Course newCourse, User creator) {
        return new UserCourse()
                .setCourse(newCourse)
                .setUser(creator)
                .setUserRole(UserCourseRole.HEAD_TEACHER)
                .setCreatedAt(LocalDateTime.now());
    }

    private UserCourse createUserCourseOnCourseJoin(Course course, User joiningUser) {
        return new UserCourse()
                .setCourse(course)
                .setUser(joiningUser)
                .setUserRole(UserCourseRole.STUDENT)
                .setCreatedAt(LocalDateTime.now());
    }
}
