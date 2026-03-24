package silverpancake.domain.entity.taskanswer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import silverpancake.domain.entity.file.File;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "task_answer")
@Data
@Accessors(chain = true)
public class TaskAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer score = 0;

    private LocalDateTime submittedAt = null;

    @Enumerated(EnumType.STRING)
    private TaskAnswerStatus status = TaskAnswerStatus.NOT_COMPLETED;

    @OneToMany(mappedBy = "taskAnswer")
    private List<File> files = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

}
