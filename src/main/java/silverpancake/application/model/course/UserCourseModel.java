package silverpancake.application.model.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import silverpancake.application.model.user.UserModel;
import silverpancake.domain.entity.user.UserCourseRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
public class UserCourseModel {
    private UserModel userModel;
    private UserCourseRole userRole;
}
