package event.nbc.event.controller;

import event.nbc.event.service.EventParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ClaimController {

    private final SimpMessagingTemplate messagingTemplate;
    private final EventParticipationService eventParticipationService;

    @MessageMapping("/claim/{eventId}")
    public void claim(@DestinationVariable Long eventId, String nickname) {
        String result = eventParticipationService.participateEvent(eventId);

        if ("SOLD_OUT".equals(result)) {
            messagingTemplate.convertAndSend("/topic/result/" + eventId, "SOLD_OUT");
        } else if ("FAILED".equals(result)) {
            messagingTemplate.convertAndSend("/topic/result/" + eventId, "FAILED");
        } else {
            messagingTemplate.convertAndSend("/topic/result/" + eventId, result);
        }
    }
}