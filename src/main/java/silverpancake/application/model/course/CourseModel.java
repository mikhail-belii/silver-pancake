package silverpancake.application.model.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import silverpancake.domain.entity.user.UserCourseRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
public class CourseModel {
    private UUID id;
    private String name;
    private String joinCode;
    private String description;
    private LocalDateTime createdAt;
    private UserCourseRole currentUserCourseRole;
}
