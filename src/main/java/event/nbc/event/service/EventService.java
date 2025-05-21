package event.nbc.event.service;

import java.util.List;

import org.springframework.stereotype.Service;

import event.nbc.aop.DistributedLock;
import event.nbc.event.dto.EventSetRequest;
import event.nbc.event.exception.EventException;
import event.nbc.event.exception.EventExceptionCode;
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
			throw new EventException(EventExceptionCode.DUPLICATED_EVENT_ID);
		}

		return s3Service.getUploadPresignedUrl(eventSetRequest.eventId(), eventSetRequest.imageName());
	}

	@DistributedLock(key = "'lock:event:' + #eventId")
	public byte[] getEvent(Long eventId) {

		Event event = redisService.getEvent(eventId);

		String imageName = event.getRemainImage();

		if (!redisService.updateEvent(event)) {
			throw new EventException(EventExceptionCode.EVENT_CRUD_FAILED);
		}

		Long id = event.getEventId();

		String presignedUrl = s3Service.getPresignedUrl(id, imageName);

		return qrUtil.toQrImage(presignedUrl, 350, 350);
	}

}
