package event.nbc.event.controller;

import event.nbc.event.service.EventParticipationServcie;
import event.nbc.event.repository.EventRepository;
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
    private final EventParticipationServcie eventParticipationServcie;

    @MessageMapping("/claim/{eventId}")
    public void claim(@DestinationVariable Long eventId, String nickname) {
        //Event event = eventRepository.findById(eventId);
        //String result = event.tryClaimImageUrlWithChance();
        String result = eventParticipationServcie.participateEvent(eventId);

        //이거 반환값이 어떤식이지??? 경로같은데 << 이 경로는어디서오는거지?
        if ("SOLD_OUT".equals(result)) {
            messagingTemplate.convertAndSend("/topic/result/" + eventId, "SOLD_OUT");
        } else if ("FAILED".equals(result)) {
            messagingTemplate.convertAndSend("/topic/result/" + eventId, "FAILED");
        } else {
            messagingTemplate.convertAndSend("/topic/result/" + eventId, result);
        }
    }
}