package silverpancake.domain.entity.draft;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import silverpancake.domain.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "draft_pick_turn")
@Data
@Accessors(chain = true)
public class DraftPickTurn {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "draft_id")
    private Draft draft;

    @Column(name = "turn_order")
    private Integer order;

}
