package event.nbc.controller;

import event.nbc.model.Event;
import event.nbc.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ClaimController {

    private final EventRepository eventRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/claim/{eventId}")
    public void claim(@DestinationVariable Long eventId, String nickname) {
        Event event = eventRepository.findById(eventId);
        String result = event.tryClaimImageUrlWithChance();

        if ("SOLD_OUT".equals(result)) {
            messagingTemplate.convertAndSend("/topic/result/" + eventId, "SOLD_OUT");
        } else if ("FAILED".equals(result)) {
            messagingTemplate.convertAndSend("/topic/result/" + eventId, "FAILED");
        } else {
            messagingTemplate.convertAndSend("/topic/result/" + eventId, result);
        }
    }
}