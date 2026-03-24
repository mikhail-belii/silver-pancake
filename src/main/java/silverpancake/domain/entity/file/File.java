package silverpancake.domain.entity.file;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file")
@Data
@Accessors(chain = true)
public class File {
    @Id
    private UUID id;

    @Length(max = 256)
    private String path;

    @Length(max = 256)
    private String originalName;

    @ManyToOne
    @JoinColumn(name = "uploader_id")
    private User uploader;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "task_answer_id")
    private TaskAnswer taskAnswer;

    private LocalDateTime createdAt;
}
