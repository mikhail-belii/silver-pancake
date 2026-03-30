package silverpancake.application.service;

import silverpancake.application.model.course.*;
import silverpancake.domain.entity.user.UserCourseRole;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    CourseModel createCourse(UUID requestingUserId, CourseCreateModel courseCreateModel);
    CourseModel editCourse(UUID requestingUserId, UUID courseId, CourseEditModel courseEditModel);
    UserCourseListModel getCourseUsers(UUID requestingUserId, UUID courseId);
    CourseModel getConcreteCourse(UUID requestingUserId, UUID courseId);
    CourseShortListModel getUserCourses(UUID requestingUserId);
    CourseModel joinCourseByCode(UUID requestingUserId, String code);
    void changeUserRoleOnCourse(
            UUID requestingUserId,
            UUID courseId,
            UUID userId,
            UserCourseRole newUserRole);
    void removeUserFromCourse(
            UUID requestingUserId,
            UUID courseId,
            UUID userId);
}
