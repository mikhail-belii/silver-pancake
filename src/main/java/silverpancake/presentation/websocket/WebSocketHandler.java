package silverpancake.presentation.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.util.JwtUtil;
import silverpancake.presentation.websocket.model.AuthenticatedSocketSession;
import silverpancake.presentation.websocket.model.ConnectModel;
import silverpancake.presentation.websocket.model.WebSocketMessage;

import java.util.UUID;

import static silverpancake.presentation.websocket.model.WebSocketMessageType.AUTH;

@Service
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final WebSocketParser webSocketParser;

    private final WebsocketSessionManager websocketSessionManager;

    private final JwtUtil jwtUtil;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        websocketSessionManager.addNewSession(AuthenticatedSocketSession.of(session));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        WebSocketMessage<?> parsedMessage = webSocketParser.parseMessage(message);

        var operationSession = websocketSessionManager.getSessionById(session.getId());

        if (operationSession.isEmpty()) {
            websocketSessionManager.returnError(session, "Не получается найти сессию");
            return;
        }

        if (!AUTH.equals(parsedMessage.getType())) {
            if (!operationSession.get().isAuthenticated()) {
                websocketSessionManager.returnError(session, "Not authenticated");
                return;
            }
        }

        switch (parsedMessage.getType()) {
            case AUTH: authenticateSession(operationSession.get(), (WebSocketMessage<ConnectModel>) parsedMessage); break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        websocketSessionManager.closeSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable error) {
        websocketSessionManager.closeSession(session);
    }

    private void authenticateSession(
            AuthenticatedSocketSession session,
            WebSocketMessage<ConnectModel> message
    ) {
        var token = message.getData().getToken();
        var claims = jwtUtil.parseAccessClaims(token);
        var authModel = new AuthorizationModel(UUID.fromString(claims.get("user_id", String.class)), token);

        session.setAuthorizationModel(authModel);
    }

}
