package silverpancake.presentation.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.presentation.websocket.model.AuthenticatedSocketSession;
import silverpancake.presentation.websocket.model.WebSocketResponseType;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class WebsocketSessionManager extends TextWebSocketHandler {

    private ConcurrentHashMap<String, AuthenticatedSocketSession> sessions = new ConcurrentHashMap<>();

    private final WebSocketParser messageParser;



    public void closeSession(WebSocketSession session) {
        sessions.remove(session.getId());
        try { session.close(); } catch (Exception ignored) {}
    }

    public void returnError(WebSocketSession session, String errorText) {
        try {
            session.sendMessage(messageParser.serializeObjectToWebSocketResponse(WebSocketResponseType.ERROR, errorText));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<AuthenticatedSocketSession> getSessionById(String id) {
        var session =  sessions.get(id);
        if (session == null) {
            return Optional.empty();
        }
        return Optional.of(session);
    }

    public void addNewSession(AuthenticatedSocketSession session) {
        sessions.put(session.getSession().getId(), session);
    }

}
