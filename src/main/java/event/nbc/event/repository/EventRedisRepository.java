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
    private final RedisTemplate<Long, Event> redisTemplate;
    private final ObjectMapper objectMapper;

    public Event findById(Long eventId) {
        Event event = redisTemplate.opsForValue().get(eventId);

        if (event == null) {
            throw new EventException(EventExceptionCode.NOT_FOUND_EVENT);
        }
        return event;
    }

    public void save(Event event) {
        try {
            Long key = event.getEventId();
            String jsonValue = objectMapper.writeValueAsString(event);
            redisTemplate.opsForValue().set(key, event);
        } catch (JsonProcessingException e) {
            throw new EventException(EventExceptionCode.EVENT_CRUD_FAILED);
        }
    }
}
