package event.nbc.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
        Long winnerCount = Optional.ofNullable(redisTemplate.opsForValue().get(winnerKey))
                .map(Long::parseLong)
                .orElse(1L);

        double currentRate = (double) winnerCount / requestCount;
        double maxRate = rate / 100.0;
        double minRate = maxRate * 0.4; // 보정 하한선: 4% 밑이면 무조건 당첨 (10의 40프로)

        if (currentRate < minRate) {
            return true;
        }

        // 현재 비율이 이미 목표 rate를 넘었다면 탈락
        if (currentRate >= maxRate) {
            return false;
        }

        // 그 외에는 랜덤 추첨
        boolean isPicked = Math.random() < (rate / 100.0);
        return isPicked;
    }

    public void increaseWinnerCount(Long eventId) { // 당첨되면 증가
        String winnerKey = "event:" + eventId + ":winnerCount";
        redisTemplate.opsForValue().increment(winnerKey);
    }

    public void clearStats(Long eventId) {
        String requestKey = "event:" + eventId + ":requestCount";
        String winnerKey = "event:" + eventId + ":winnerCount";

        redisTemplate.delete(requestKey);
        redisTemplate.delete(winnerKey);
    }
}
