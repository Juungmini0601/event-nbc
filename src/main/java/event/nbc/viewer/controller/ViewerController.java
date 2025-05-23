package event.nbc.viewer.controller;

import event.nbc.event.repository.EventRedisRepository;
import event.nbc.model.Event;
import event.nbc.viewer.service.ViewerSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Controller
public class ViewerController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ViewerSessionService viewerSessionService;
    private final EventRedisRepository eventRedisRepository;

    @MessageMapping("/enter/{eventId}")
    public void handleEnter(@DestinationVariable Long eventId, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        int count = viewerSessionService.addSession(eventId, sessionId);

        // 이벤트에서 남은 선물 개수 가져오기
        Event event = eventRedisRepository.findById(eventId);
        int remainingGifts = event.getRemainingCount();

        // 뷰어 수와 남은 선물 개수를 맵에 담기
        Map<String, Integer> data = new HashMap<>();
        data.put("viewerCount", count);
        data.put("remainingGifts", remainingGifts);

        // 기존 broadcast 유지 (맵으로 변경)
        messagingTemplate.convertAndSend("/topic/viewers/" + eventId, data);

        // 접속한 본인에게도 직접 보내기 (맵으로 변경)
        messagingTemplate.convertAndSendToUser(
                Objects.requireNonNull(sessionId),
                "/queue/viewer-count/" + eventId,
                data
        );
    }
}
