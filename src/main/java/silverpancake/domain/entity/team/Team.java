package silverpancake.domain.entity.team;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.user.User;
import silverpancake.domain.entity.userteam.UserTeam;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "team")
@Data
@Accessors(chain = true)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private User captain;

    @OneToMany(mappedBy = "team")
    private List<UserTeam> teamMembers;

    private String name;
}
