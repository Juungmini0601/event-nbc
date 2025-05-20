package event.nbc.event.service;

import event.nbc.event.exception.EventException;
import event.nbc.event.exception.EventExceptionCode;
import event.nbc.event.repository.EventRedisRepository;
import event.nbc.model.Event;
import event.nbc.redis.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventRedisRepository eventRepository;
    private final RandomGenerator randomGenerator;
    private static final int WINNING_PERCENTAGE = 20; //당첨확률
    private final RedisLockService lockService;

    public String participateEvent(Long eventId){
        String lockKey = "lock:event:" + eventId;

        String lockValue = lockService.lock(lockKey);
        if (lockValue == null) { //락 획득 못하면 예외
            throw new EventException(EventExceptionCode.LOCK_FAILED);
        }

        try{
            Event event = eventRepository.findById(eventId);

            if( !event.isEventActive() ){
                throw new EventException(EventExceptionCode.INVALID_EVENT);
            }

            if (event.getRemainingCount() < 1) {
                return "SOLD_OUT";
            }

            if (!randomGenerator.tryPick(eventId, WINNING_PERCENTAGE)) {
                // 실패 이미지 반환
                return "FAILED";
            }
            //당첨
            //이미지 가져와서 반환
            String winnerImg = event.getImageUrls().getLast();

            event.decreaseRemainingCount();
            if(event.getRemainingCount() < 1){
                randomGenerator.clearStats(eventId);
            }
            eventRepository.save(event);
            randomGenerator.increaseWinnerCount(eventId);

            return winnerImg;
        }finally {
            lockService.unlock(lockKey,lockValue);
        }
    }
}
