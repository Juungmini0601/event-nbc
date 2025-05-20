package event.nbc.event.repository;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import event.nbc.event.exception.EventException;
import event.nbc.event.exception.EventExceptionCode;
import event.nbc.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRedisRepository{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public Event findById(Long eventId) {
        String key = String.valueOf(eventId);
        Object raw = redisTemplate.opsForValue().get(key);

        if (raw == null) {
            throw new EventException(EventExceptionCode.NOT_FOUND_EVENT);
        }

        try {
            String jsonValue = raw.toString();
            return objectMapper.readValue(jsonValue, Event.class);
        } catch (JsonProcessingException e) {
            throw new EventException(EventExceptionCode.EVENT_CRUD_FAILED);
        }
    }

    public void save(Event event) {
        try {
            String key = String.valueOf(event.getEventId());
            String jsonValue = objectMapper.writeValueAsString(event);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (JsonProcessingException e) {
            throw new EventException(EventExceptionCode.EVENT_CRUD_FAILED);
        }
    }
}
