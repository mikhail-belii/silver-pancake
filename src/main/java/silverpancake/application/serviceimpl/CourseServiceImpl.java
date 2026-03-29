package silverpancake.application.serviceimpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.CourseMapper;
import silverpancake.application.model.course.*;
import silverpancake.application.repository.CourseRepository;
import silverpancake.application.repository.UserCourseRepository;
import silverpancake.application.repository.UserRepository;
import silverpancake.application.service.CourseService;
import silverpancake.application.util.CourseCodeGenerator;
import silverpancake.application.util.CourseUtility;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.user.User;
import silverpancake.domain.entity.user.UserCourseRole;
import silverpancake.domain.entity.usercourse.UserCourse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl  implements CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;
    private final CourseCodeGenerator courseCodeGenerator;
    private final ExceptionUtility exceptionUtility;
    private final CourseMapper courseMapper;

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
                .orElseThrow(exceptionUtility::userNotCourseMemberException);

        if (!CourseUtility.isCourseAvailableForEditing(course, user)) {
            throw exceptionUtility.securityException();
        }

        course.setName(courseEditModel.getName())
                .setDescription(courseEditModel.getDescription());

        courseRepository.save(course);

        return courseMapper.toModel(course, userCourse.getUserRole());
    }

    @Override
    public List<UserCourseModel> getCourseUsers(UUID requestingUserId, UUID courseId) {
        return List.of();
    }

    @Override
    public CourseModel getConcreteCourse(UUID requestingUserId, UUID courseId) {
        return null;
    }

    @Override
    public List<CourseShortModel> getUserCourses(UUID requestingUserId, boolean isArchived) {
        return List.of();
    }

    @Override
    public void joinCourseByCode(UUID requestingUserId, String code) {

    }

    @Override
    public void changeUserRoleOnCourse(UUID requestingUserId, UUID courseId, UUID userId, UserCourseRole newUserRole) {

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
}
