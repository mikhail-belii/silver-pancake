package silverpancake.presentation.websocket.model.draft;

import lombok.Data;
import lombok.experimental.Accessors;
import silverpancake.application.model.draft.DraftPickTurnModel;

import java.util.List;

@Data
@Accessors(chain = true)
public class OrderOfSelectionChangedModel {

    private List<DraftPickTurnModel> draftPickTurnModels;

}
