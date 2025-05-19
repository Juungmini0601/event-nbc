package event.nbc.viewer.handler;

import event.nbc.viewer.service.ViewerSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RequiredArgsConstructor
@Component
public class WebSocketDisconnectHandler implements ApplicationListener<SessionDisconnectEvent> {
    private final SimpMessagingTemplate messagingTemplate;
    private final ViewerSessionService viewerSessionService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        int updatedCount = viewerSessionService.removeSession(sessionId);

        if (updatedCount >= 0) {
            Long eventId = viewerSessionService.getSessionIdToEventMap().get(sessionId);
            if (eventId != null) {
                messagingTemplate.convertAndSend("/topic/viewers/" + eventId, updatedCount);
            }
        }
    }
}