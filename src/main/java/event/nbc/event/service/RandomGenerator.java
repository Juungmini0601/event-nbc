package event.nbc.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RandomGenerator {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean tryPick(Long eventId, int rate) {
        String requestKey = "event:" + eventId + ":requestCount";
        String winnerKey = "event:" + eventId + ":winnerCount";

        // 요청 카운트 증가 (어차피 이벤트 불러올때 = 요청 이니깐)
        Long requestCount = redisTemplate.opsForValue().increment(requestKey);

        // 현재까지의 당첨자 수 가져오기
        String winnerStr = redisTemplate.opsForValue().get(winnerKey);
        Long winnerCount = (winnerStr != null) ? Long.parseLong(winnerStr) : 0L;

        // 현재 비율로 제한 ( 계속 당첨당첨 붙지도않고 몰리지도 않고 일정 비율(20퍼 해놧음) 으로 xxxoxxxoxx << 이런식?
        double currentRate = (double) winnerCount / requestCount;
        if (currentRate >= (rate / 100.0)) {
            return false;
        }
        
        return true;
    }

    public void increaseWinnerCount(String eventId) { // 당첨되면 증가
        String winnerKey = "event:" + eventId + ":winnerCount";
        redisTemplate.opsForValue().increment(winnerKey);
    }
}
