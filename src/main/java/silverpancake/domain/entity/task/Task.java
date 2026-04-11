package silverpancake.domain.entity.task;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.draft.Draft;
import silverpancake.domain.entity.file.File;
import silverpancake.domain.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "task")
@Getter
@Setter
@Accessors(chain = true)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Length(max = 2048)
    private String text;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToOne(mappedBy = "task")
    private Draft draft;

    @OneToMany(mappedBy = "task")
    private List<File> files;

    private LocalDateTime createdAt;

    private LocalDateTime deadline;

    private Integer maxScore;

    @Enumerated(EnumType.STRING)
    private TeamFormationType teamFormationType;
}
