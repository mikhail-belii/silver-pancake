package silverpancake.presentation.websocket.model.draft;

import lombok.Data;
import silverpancake.application.model.team.TeamModel;

import java.util.List;
import java.util.UUID;

@Data
public class DraftStartedModel {

    private List<UUID> chooseOrderCaptainIds;

    private List<TeamModel> teams;

}
