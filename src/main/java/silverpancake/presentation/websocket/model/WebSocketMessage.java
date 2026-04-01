package silverpancake.presentation.websocket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSocketMessage<T> {

    private WebSocketMessageType type;

    private T data;

}
