package silverpancake.presentation.websocket.model.draft;

import lombok.Data;
import lombok.experimental.Accessors;
import silverpancake.application.model.team.TeamModel;

@Data
@Accessors(chain = true)
public class TeamStructureChanged {

    private TeamModel changedTeam;

}
