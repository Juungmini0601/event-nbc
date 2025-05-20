package event.nbc.event.service;

import event.nbc.event.exception.EventException;
import event.nbc.event.exception.EventExceptionCode;
import event.nbc.event.repository.EventRedisRepository;
import event.nbc.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventParticipationServcie {

    private final EventRedisRepository eventRepository;
    private final RandomGenerator randomGenerator;

    public String participateEvent(Long eventId){

        Event event = eventRepository.findById(eventId);

        if( !event.isEventActive() ){
            throw new EventException(EventExceptionCode.INVALID_EVENT);
        }

        if (event.getRemainingCount() < 1) {
            return "SOLD_OUT";
        }

        if (!randomGenerator.tryPick(eventId, 20)) {
            // 실패 이미지 반환
            return "FAILED";
        }
        //당첨
        //이미지 가져와서 반환
        String winnerImg = event.getImageUrls().getLast();

        event.decreaseRemakingCount();
        if(event.getRemainingCount() < 1){
            randomGenerator.clearStats(eventId);
        }
        eventRepository.save(event);
        randomGenerator.increaseWinnerCount(eventId);

        return winnerImg;
    }
}
