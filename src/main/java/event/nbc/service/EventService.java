package event.nbc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import event.nbc.dto.EventSetRequest;
import event.nbc.model.Event;
import event.nbc.qr.QrUtil;
import event.nbc.redis.RedisService;
import event.nbc.s3.S3Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

	private final RedisService redisService;
	private final S3Service s3Service;
	private final QrUtil qrUtil;

	public List<String> setEvent(EventSetRequest eventSetRequest) {

		Event event = Event.builder()
			.eventId(eventSetRequest.eventId())
			.remainingCount(eventSetRequest.imageName().size())
			.imageUrls(eventSetRequest.imageName())
			.startAt(eventSetRequest.startAt())
			.endAt(eventSetRequest.endAt())
			.build();

		if (!redisService.setEvent(event)) {
			throw new RuntimeException("이벤트 저장 실패");
		}

		return s3Service.getUploadPresignedUrl(eventSetRequest.eventId(), eventSetRequest.imageName());
	}

	// TODO : 동시성 작업 필요, AOP 만들어지면 적용 예정
	public byte[] getEvent(Long eventId) {

		Event event = redisService.getEvent(eventId);

		String imageName = event.getRemainImage();

		if (!redisService.updateEvent(event)) {
			throw new RuntimeException("이벤트 업데이트 실패");
		}

		Long id = event.getEventId();

		String presignedUrl = s3Service.getPresignedUrl(id, imageName);

		return qrUtil.toQrImage(presignedUrl, 350, 350);
	}

}
