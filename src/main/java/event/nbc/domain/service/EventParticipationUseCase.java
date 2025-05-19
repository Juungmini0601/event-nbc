package event.nbc.domain.service;

import event.nbc.domain.exception.EventException;
import event.nbc.domain.exception.EventExceptionCode;
import event.nbc.domain.event.repository.EventRepository;
import event.nbc.domain.vo.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EventParticipationUseCase {

    private final EventRepository eventRepository;
    private final S3Service s3Service;

    public byte[] participateEvent(Long eventId){

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventException(EventExceptionCode.NOT_FOUND_EVENT));

        if( !event.isEventActive() ){
            throw new EventException(EventExceptionCode.INVALID_EVENT);
        }

        boolean pick= isWinner(2);
        if (!pick) {
            // 실패 이미지 반환
            String failImgPath = "실패 이미지 경로를 s3에 저장하기?";
            return s3Service.getImageBytes(failImgPath);
        }

        int remainCount = (int) event.getRemainingCount().decrementAndGet();
        if (remainCount < 0) {
            throw new EventException(EventExceptionCode.EVENT_OUT_OF_STOCK);
        }

        //당첨
        //이미지 가져와서 반환
        eventRepository.save(event);
        String winnerImg = event.getImageUrls().getLast();
        return s3Service.getImageBytes(winnerImg);

    }

    public boolean isWinner(int successRatePercent) {
        int random = ThreadLocalRandom.current().nextInt(100); // 0 ~ 99
        return random < successRatePercent;
    }
}
