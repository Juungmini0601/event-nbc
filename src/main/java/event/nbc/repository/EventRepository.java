package event.nbc.repository;

import event.nbc.model.Event;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 나중에 삭제해야함
@Component
public class EventRepository {
    private final Map<Long, Event> eventMap = new ConcurrentHashMap<>();

    public void save(Event event) {
        eventMap.put(event.getEventId(), event);
    }

    public Event findById(Long id) {
        return eventMap.get(id);
    }
}
