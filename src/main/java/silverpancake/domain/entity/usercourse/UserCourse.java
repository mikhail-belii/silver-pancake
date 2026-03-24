package silverpancake.domain.entity.usercourse;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.user.User;
import silverpancake.domain.entity.user.UserCourseRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_course")
@Data
@Accessors(chain = true)
public class UserCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    private UserCourseRole userRole;

    private LocalDateTime createdAt;
}
