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
    public void handleClaim(@DestinationVariable Long eventId, String nickname) {
        Event event = eventRepository.findById(eventId);
        if (event == null) return;

        String imageUrl = event.tryClaimImageUrlWithChance();
        messagingTemplate.convertAndSend(
                "/topic/result/" + eventId,
                imageUrl != null ? imageUrl : "FAILED"
        );
    }
}