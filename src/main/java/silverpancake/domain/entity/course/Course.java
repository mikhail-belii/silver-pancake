package silverpancake.domain.entity.course;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import silverpancake.domain.entity.usercourse.UserCourse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "course")
@Data
@Accessors(chain = true)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Length(max = 128)
    private String name;

    @Length(min = 8, max = 8)
    @Column(unique = true)
    private String joinCode;

    @Length(max = 512)
    private String description;

    @OneToMany(mappedBy = "course")
    private List<UserCourse> courseUsers;

    private LocalDateTime createdAt;
}