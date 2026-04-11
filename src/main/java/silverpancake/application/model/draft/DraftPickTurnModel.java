package silverpancake.application.model.draft;

import lombok.Data;
import lombok.experimental.Accessors;
import silverpancake.application.model.user.UserModel;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class DraftPickTurnModel {

    private UUID id;

    private UserModel user;

}
