package silverpancake.presentation.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.presentation.websocket.model.*;

@Service
@RequiredArgsConstructor
public class WebSocketParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ExceptionUtility exceptionUtility;

    public WebSocketMessage<?> parseMessage(TextMessage message) {
        if (message == null || message.getPayloadLength() == 0) {
            throw exceptionUtility.invalidWebsocketMessageException();
        }
        WebSocketMessage<?> result = null;
        try {
            result = objectMapper.readValue(message.getPayload(), new TypeReference<WebSocketMessage<?>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Class<?> clazz = geClassByMessageType(result.getType());
        return new WebSocketMessage<>()
                .setData(objectMapper.convertValue(result.getData(), clazz))
                .setType(result.getType());
    }

    private Class<?> geClassByMessageType(WebSocketMessageType type) {
        return switch (type) {
            case AUTH -> ConnectModel.class;
        };
    }

    public TextMessage serializeObjectToWebSocketResponse(WebSocketResponseType type, Object object) {
        try {
            return new TextMessage(objectMapper.writeValueAsString(new WebSocketResponse().setData(object).setType(type)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
