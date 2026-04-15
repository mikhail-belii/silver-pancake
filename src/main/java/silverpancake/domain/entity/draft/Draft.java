package silverpancake.domain.entity.draft;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.team.Team;
import silverpancake.domain.entity.user.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "draft")
@Getter
@Setter
@Accessors(chain = true)
public class Draft {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "draft")
    private List<DraftPickTurn> draftPickTurns;

    @OneToOne
    @JoinColumn(name = "current_selecting_captain_id", referencedColumnName = "id")
    private User currentSelectingCaptain;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;

    @OneToMany(mappedBy = "draft")
    private List<Team> teams;

    private Boolean isEnded;

    private Boolean isStarted;

    private LocalDateTime lastPickTime;

    public Long getTimeToPick() {
        if (lastPickTime == null) {
            return null;
        }
        Duration duration = Duration.between(lastPickTime, LocalDateTime.now());
        return Math.max(60L - duration.getSeconds(), 0);
    }

}
