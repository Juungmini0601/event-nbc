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

	//TODO : (임시용) 그냥 저장만 할려고 대충 짠거니 추후 수정 필요
	public Boolean setEvent(Event event) {

		Duration ttl = Duration.between(LocalDateTime.now(), event.getEndAt());

		return template.opsForValue().setIfAbsent(event.getEventId(), event, ttl);
	}

	//임시 redis 조회
	public Event getEvent(Long eventId) {

		return template.opsForValue().get(eventId);
	}

	public Boolean updateEvent(Event event) {

		Duration ttl = Duration.between(LocalDateTime.now(), event.getEndAt());

		return template.opsForValue().setIfPresent(event.getEventId(), event, ttl);
	}
}
