package silverpancake.presentation.websocket.model;

import lombok.Data;

import java.util.UUID;

@Data
public class ConnectModel {

    private String token;

    private UUID observableDraftId;

}
