package silverpancake.application.mapper;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import silverpancake.application.model.course.UserCourseModel;
import silverpancake.domain.entity.usercourse.UserCourse;

@UtilityClass
@ExtensionMethod(SimpleUserMapper.class)
public class UserCourseMapper {
    public UserCourseModel toModel(UserCourse userCourseEntity) {
        if (userCourseEntity == null) {
            return null;
        }
        return new UserCourseModel()
                .setUserModel(userCourseEntity.getUser().toModel())
                .setUserRole(userCourseEntity.getUserRole());
    }
}
