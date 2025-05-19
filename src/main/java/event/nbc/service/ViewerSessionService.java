package event.nbc.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ViewerSessionService {
    @Getter
    private final Map<Long, Set<String>> viewerSessionMap = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, Long> sessionIdToEventMap = new ConcurrentHashMap<>();

    public int addSession(Long eventId, String sessionId) {
        viewerSessionMap.computeIfAbsent(eventId, id -> ConcurrentHashMap.newKeySet()).add(sessionId);
        sessionIdToEventMap.put(sessionId, eventId);
        return viewerSessionMap.get(eventId).size();
    }

    public int removeSession(String sessionId) {
        Long eventId = sessionIdToEventMap.remove(sessionId);
        if (eventId != null) {
            Set<String> sessions = viewerSessionMap.get(eventId);
            if (sessions != null) {
                sessions.remove(sessionId);
                return sessions.size();
            }
        }
        return -1;
    }
}
