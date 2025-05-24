package event.nbc.viewer.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ViewerSessionService {
    @Getter
    private final Map<Long, Set<String>> viewerSessionMap = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, Long> sessionIdToEventMap = new ConcurrentHashMap<>();
    private final StringRedisTemplate redis;

    public int addSession(Long eventId, String sessionId) {
        viewerSessionMap.computeIfAbsent(eventId, id -> ConcurrentHashMap.newKeySet()).add(sessionId);
        sessionIdToEventMap.put(sessionId, eventId);

        redis.opsForSet().add("event:" + eventId + ":sessions", sessionId);
        redis.opsForValue().set("session:" + sessionId, String.valueOf(eventId));

        Long count = redis.opsForSet().size("event:" + eventId + ":sessions");
        return count != null ? count.intValue() : 0;
    }

    public int removeSession(String sessionId) {
        Long eventId = sessionIdToEventMap.get(sessionId);
        if (eventId != null) {
            Set<String> sessions = viewerSessionMap.get(eventId);
            if (sessions != null) {
                sessions.remove(sessionId);
            }
            sessionIdToEventMap.remove(sessionId);
            redis.opsForSet().remove("event:" + eventId + ":sessions", sessionId);
            redis.delete("session:" + sessionId);

            Long count = redis.opsForSet().size("event:" + eventId + ":sessions");
            return count != null ? count.intValue() : 0;
        }
        return -1;
    }
}
