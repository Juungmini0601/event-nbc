package event.nbc.redis;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import event.nbc.model.Event;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<Long, Event> template;

	public Boolean setEvent(Event event) {

		Duration ttl = Duration.between(
			java.time.Instant.now()
				.atZone(java.time.ZoneId.of("Asia/Seoul"))
				.toLocalDateTime(), 
			event.getEndAt());

		return template.opsForValue().setIfAbsent(event.getEventId(), event, ttl);
	}

	public Event getEvent(Long eventId) {

		return template.opsForValue().get(eventId);
	}

	public Boolean updateEvent(Event event) {

		Duration ttl = Duration.between(
			java.time.Instant.now()
				.atZone(java.time.ZoneId.of("Asia/Seoul"))
				.toLocalDateTime(), 
			event.getEndAt());

		return template.opsForValue().setIfPresent(event.getEventId(), event, ttl);
	}
}
