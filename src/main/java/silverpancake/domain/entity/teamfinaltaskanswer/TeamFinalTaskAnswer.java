package silverpancake.domain.entity.teamfinaltaskanswer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.taskanswer.TaskAnswerStatus;
import silverpancake.domain.entity.team.Team;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "team_final_task_answer")
@Getter
@Setter
@Accessors(chain = true)
public class TeamFinalTaskAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer score = 0;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private TaskAnswerStatus status = TaskAnswerStatus.NOT_COMPLETED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "final_task_answer_id", unique = true)
    private TaskAnswer finalTaskAnswer;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
