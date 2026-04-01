package silverpancake.application.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import silverpancake.application.model.user.UserRegisterModel;
import silverpancake.application.repository.CourseRepository;
import silverpancake.application.repository.UserCourseRepository;
import silverpancake.application.repository.UserRepository;
import silverpancake.application.service.AuthService;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.user.UserCourseRole;
import silverpancake.domain.entity.usercourse.UserCourse;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PredefinedDataRunner implements ApplicationRunner {
    private final PredefinedDataProperties predefinedDataProperties;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!predefinedDataProperties.isEnabled()
                || predefinedDataProperties.getCourse() == null
                || predefinedDataProperties.getUsers() == null
                || predefinedDataProperties.getUsers().isEmpty()) {
            return;
        }

        for (var predefinedUser : predefinedDataProperties.getUsers()) {
            if (userRepository.existsByEmail(predefinedUser.getEmail())) {
                continue;
            }

            var userRegisterModel = new UserRegisterModel();
            userRegisterModel.setEmail(predefinedUser.getEmail());
            userRegisterModel.setPassword(predefinedUser.getPassword());
            userRegisterModel.setFirstName(predefinedUser.getFirstName());
            userRegisterModel.setLastName(predefinedUser.getLastName());
            authService.register(userRegisterModel);
        }

        var course = courseRepository.findByJoinCode(predefinedDataProperties.getCourse().getJoinCode())
                .orElseGet(this::createPredefinedCourse);

        for (var predefinedUser : predefinedDataProperties.getUsers().subList(1, predefinedDataProperties.getUsers().size())) {
            var user = userRepository.findByEmail(predefinedUser.getEmail())
                    .orElseThrow();
            if (userCourseRepository.findByUserAndCourse(user.getId(), course.getId()).isPresent()) {
                continue;
            }

            userCourseRepository.save(new UserCourse()
                    .setCourse(course)
                    .setUser(user)
                    .setUserRole(UserCourseRole.STUDENT)
                    .setCreatedAt(LocalDateTime.now()));
        }
    }

    private Course createPredefinedCourse() {
        var ownerEmail = predefinedDataProperties.getUsers().getFirst().getEmail();
        var owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow();

        var courseProperties = predefinedDataProperties.getCourse();
        var course = courseRepository.save(new Course()
                .setName(courseProperties.getName())
                .setDescription(courseProperties.getDescription())
                .setJoinCode(courseProperties.getJoinCode())
                .setCreatedAt(LocalDateTime.now()));

        userCourseRepository.save(new UserCourse()
                .setCourse(course)
                .setUser(owner)
                .setUserRole(UserCourseRole.HEAD_TEACHER)
                .setCreatedAt(LocalDateTime.now()));

        return course;
    }
}
