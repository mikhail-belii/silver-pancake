package silverpancake.presentation.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.model.draft.DraftModel;
import silverpancake.presentation.websocket.model.*;
import silverpancake.presentation.websocket.model.draft.DraftEndedModel;
import silverpancake.presentation.websocket.model.draft.OrderOfSelectionChangedModel;
import silverpancake.presentation.websocket.model.draft.TeamStructureChanged;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebSocketSender {

    private final WebSocketParser webSocketParser;

    private final WebsocketSessionManager websocketSessionManager;

    public void sendDraftStartedMessage(DraftModel draftModel) {
        List<AuthenticatedSocketSession> sessionsToSend = websocketSessionManager.getSessionsByDraftId(draftModel.getId());

        sessionsToSend.forEach(session -> {
            try {
                session.getSession().sendMessage(webSocketParser
                        .serializeObjectToWebSocketResponse(WebSocketResponseType.DRAFT_STARTED, draftModel));
            } catch (IOException e) {
                websocketSessionManager.returnError(session.getSession(), e.getMessage());
            }
        });
    }

    public void sendAutoSelectionPerformedMessage(UUID userId, UUID draftId) {
        List<AuthenticatedSocketSession> sessionsToSend = websocketSessionManager.getSessionsByUserId(userId, draftId);

        sessionsToSend.forEach(session -> {
            try {
                session.getSession().sendMessage(webSocketParser
                        .serializeObjectToWebSocketResponse(WebSocketResponseType.AUTO_SELECTION_PERFORMED, null));
            } catch (IOException e) {
                websocketSessionManager.returnError(session.getSession(), e.getMessage());
            }
        });
    }

    public void sendOrderOfSelectionChangedMessage(
            OrderOfSelectionChangedModel orderOfSelectionChangedModel,
            UUID draftId
    ) {
        List<AuthenticatedSocketSession> sessionsToSend = websocketSessionManager.getSessionsByDraftId(draftId);

        sessionsToSend.forEach(session -> {
            try {
                session.getSession().sendMessage(webSocketParser
                        .serializeObjectToWebSocketResponse(WebSocketResponseType.ORDER_OF_SELECTION_CHANGED, orderOfSelectionChangedModel));
            } catch (IOException e) {
                websocketSessionManager.returnError(session.getSession(), e.getMessage());
            }
        });
    }

    public void sendTeamStructureChangedMessage(
            TeamStructureChanged teamStructureChanged,
            UUID draftId
    ) {
        List<AuthenticatedSocketSession> sessionsToSend = websocketSessionManager.getSessionsByDraftId(draftId);

        sessionsToSend.forEach(session -> {
            try {
                session.getSession().sendMessage(webSocketParser
                        .serializeObjectToWebSocketResponse(WebSocketResponseType.TEAM_STRUCTURE_CHANGED, teamStructureChanged));
            } catch (IOException e) {
                websocketSessionManager.returnError(session.getSession(), e.getMessage());
            }
        });
    }

    public void sendDraftEndedMessage(
            UUID draftId
    ) {
        List<AuthenticatedSocketSession> sessionsToSend = websocketSessionManager.getSessionsByDraftId(draftId);

        sessionsToSend.forEach(session -> {
            try {
                session.getSession().sendMessage(webSocketParser
                        .serializeObjectToWebSocketResponse(WebSocketResponseType.DRAFT_ENDED, null));
            } catch (IOException e) {
                websocketSessionManager.returnError(session.getSession(), e.getMessage());
            }
        });
    }
}
