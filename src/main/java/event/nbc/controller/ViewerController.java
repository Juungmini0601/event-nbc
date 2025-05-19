package event.nbc.controller;

import event.nbc.service.ViewerSessionService;
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
        messagingTemplate.convertAndSend("/topic/viewers/" + eventId, count);
    }
}