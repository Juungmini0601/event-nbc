package event.nbc.event.controller;

import event.nbc.event.repository.EventRedisRepository;
import event.nbc.event.service.EventParticipationService;
import event.nbc.model.Event;
import event.nbc.viewer.service.ViewerSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Controller
public class ClaimController {

    private final SimpMessagingTemplate messagingTemplate;
    private final EventParticipationService eventParticipationService;
    private final EventRedisRepository eventRedisRepository;
    private final ViewerSessionService viewerSessionService;

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

        // 이벤트에서 남은 선물 개수 가져오기
        Event event = eventRedisRepository.findById(eventId);
        int remainingGifts = event.getRemainingCount();

        // 현재 접속자 수 가져오기
        Set<String> sessions = viewerSessionService.getViewerSessionMap().get(eventId);
        int viewerCount = sessions != null ? sessions.size() : 0;

        // 뷰어 수와 남은 선물 개수를 맵에 담기
        Map<String, Integer> data = new HashMap<>();
        data.put("viewerCount", viewerCount);
        data.put("remainingGifts", remainingGifts);

        // 모든 사용자에게 업데이트된 정보 전송
        messagingTemplate.convertAndSend("/topic/viewers/" + eventId, data);
    }
}