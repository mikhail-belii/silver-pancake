package silverpancake.presentation.websocket.model.draft;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderOfSelectionChangedModel {

    private List<UUID> chooseOrderCaptainIds;

}
