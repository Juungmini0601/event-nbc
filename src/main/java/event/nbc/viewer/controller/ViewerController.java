package event.nbc.viewer.controller;

import event.nbc.viewer.service.ViewerSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ViewerController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ViewerSessionService viewerSessionService;

    @MessageMapping("/enter/{eventId}")
    public void handleEnter(@DestinationVariable Long eventId, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        int count = viewerSessionService.addSession(eventId, sessionId);

        // 기존 broadcast 유지
        messagingTemplate.convertAndSend("/topic/viewers/" + eventId, count);
    }
}