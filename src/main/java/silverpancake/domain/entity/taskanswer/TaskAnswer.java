package silverpancake.domain.entity.taskanswer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import silverpancake.domain.entity.file.File;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "task_answer")
@Getter
@Setter
@Accessors(chain = true)
public class TaskAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "taskAnswer")
    private List<File> files = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToMany
    @JoinTable(
            name = "task_answer_vote",
            joinColumns = @JoinColumn(name = "task_answer_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> votedUsers = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;
}
