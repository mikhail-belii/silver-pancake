package silverpancake.presentation.websocket.model;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;
import silverpancake.application.model.auth.AuthorizationModel;

@Data
public class AuthenticatedSocketSession {

    private WebSocketSession session;

    private AuthorizationModel authorizationModel;

    public AuthenticatedSocketSession(WebSocketSession session) {
        this.session = session;
    }

    public boolean isAuthenticated() {
        return authorizationModel != null;
    }

    public static AuthenticatedSocketSession of(
            WebSocketSession session
    ) {
        return new AuthenticatedSocketSession(session);
    }

}
