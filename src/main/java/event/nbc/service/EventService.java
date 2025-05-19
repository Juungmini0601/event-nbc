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

		redisService.setEvent(event);

		return s3Service.getUploadPresignedUrl(eventSetRequest.eventId(), eventSetRequest.imageName());
	}

	// TODO : 동시성 작업 필요, 예외처리 안되어있음, 읽은 후 remaining_count 변경 저장 로직 구현 x
	public byte[] getEvent(Long eventId) throws Exception {

		Event event = redisService.getEvent(eventId);

		Long id = event.getEventId();

		String imageName = event.getRemainImage();

		String presignedUrl = s3Service.getPresignedUrl(id, imageName);

		return qrUtil.toQrImage(presignedUrl, 500, 500);
	}

}
