package event.nbc.event.service;

import event.nbc.event.exception.EventException;
import event.nbc.event.exception.EventExceptionCode;
import event.nbc.event.repository.EventRedisRepository;
import event.nbc.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EventParticipationServcie {

    private final EventRedisRepository eventRepository;
    private final S3Service s3Service;

    public String participateEvent(Long eventId){

        Event event = eventRepository.findById(eventId);

        if( !event.isEventActive() ){
            throw new EventException(EventExceptionCode.INVALID_EVENT);
        }

        boolean pick= isWinner(20);
        if (!pick) {
            // 실패 이미지 반환
            String failImgPath = "실패 이미지 경로";
            //return s3Service.getImageBytes(failImgPath);
            return "FAILED";
        }

        int remainCount = (int) event.getRemainingCount();
        if (remainCount < 1) {
            //throw new EventException(EventExceptionCode.EVENT_OUT_OF_STOCK);
            return "SOLD_OUT";
        }
        //당첨
        //이미지 가져와서 반환
        String winnerImg = event.getImageUrls().getLast();
        event.decreaseRemakingCount();
        eventRepository.save(event);
        //return s3Service.getImageBytes(winnerImg);
        return winnerImg;
    }

    public boolean isWinner(int successRatePercent) {
        int random = ThreadLocalRandom.current().nextInt(100); // 0 ~ 99
        return random < successRatePercent;
    }
}
