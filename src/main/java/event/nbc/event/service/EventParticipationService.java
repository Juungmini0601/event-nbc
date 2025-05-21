package event.nbc.event.service;

import event.nbc.aop.DistributedLock;
import java.util.Base64;

import event.nbc.event.exception.EventException;
import event.nbc.event.exception.EventExceptionCode;
import event.nbc.event.repository.EventRedisRepository;
import event.nbc.model.Event;
import event.nbc.service.EventService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventRedisRepository eventRepository;
    private final RandomGenerator randomGenerator;
    private static final int WINNING_PERCENTAGE = 10; //당첨확률
    private final EventService eventService;

    @DistributedLock(key = "'lock:event:' + #eventId")
    public String participateEvent(Long eventId) {
        Event event = eventRepository.findById(eventId);
        if (!event.isEventActive()) {
            throw new EventException(EventExceptionCode.INVALID_EVENT);
        }

        long remainCnt = event.getRemainingCount();
        if (remainCnt < 1) {
            return "SOLD_OUT";
        }

        if (!randomGenerator.tryPick(eventId, WINNING_PERCENTAGE)) {
            // 실패 이미지 반환
            return "FAILED";
        }

        String base64 = Base64.getEncoder().encodeToString(eventService.getEvent(eventId));
        String winnerImg = "data:image/png;base64," + base64;
        if (event.getRemainingCount() < 1) {
            randomGenerator.clearStats(eventId);
        }

        randomGenerator.increaseWinnerCount(eventId);
        return winnerImg;
    }
}
