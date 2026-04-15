package silverpancake.application.model.draft;

import lombok.Data;
import lombok.experimental.Accessors;
import silverpancake.application.model.team.TeamModel;
import silverpancake.application.model.user.UserModel;

import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class DraftModel {

    private UUID id;

    private UserModel currentSelectingUser;

    private List<DraftPickTurnModel> draftPickTurns;

    private List<TeamModel> teams;

    private Boolean isStarted;

    private Boolean isEnded;

    private Long timeToPick;

}
