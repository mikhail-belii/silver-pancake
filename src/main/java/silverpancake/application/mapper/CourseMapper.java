package silverpancake.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import silverpancake.application.model.course.CourseModel;
import silverpancake.application.model.course.CourseShortModel;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.user.UserCourseRole;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(source = "userCourseRole", target = "currentUserCourseRole")
    CourseModel toModel(Course courseEntity, UserCourseRole userCourseRole);
    CourseShortModel toShortModel(Course courseEntity);
}
