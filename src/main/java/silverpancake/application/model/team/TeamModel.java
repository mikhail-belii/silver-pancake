package silverpancake.application.model.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import silverpancake.application.model.user.UserModel;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
public class TeamModel {
    private UUID id;
    private String name;
    private UserModel captain;
    private List<UserModel> members;
}
