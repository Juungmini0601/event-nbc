package event.nbc.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisLockService {
    private final RedisTemplate<String, String> redisTemplate;

    public String lock(String key) {
        long baseWait = 70;
        long jitter = ThreadLocalRandom.current().nextLong(-20, 21); // -20 ~ +20

        final long waitMillis = baseWait + jitter; //락 시도 전까지 쉬는시간
        final int maxRetries = 100; //리트 횟수

        String lockValue = UUID.randomUUID().toString(); //쓰레드별로 다른 락 키 값을 줘야함 (다른 쓰레드 지워버릴수도잇음)

        for (int i = 0; i < maxRetries; i++) { //락 얻기 시도
            Boolean success = redisTemplate
                    .opsForValue()
                    .setIfAbsent(key, lockValue, Duration.ofMillis(300));

            if (Boolean.TRUE.equals(success)) { //락 얻음
                return lockValue;
            }

            try { //못얻으면 waitMillis ms 초 쉬고 다시 리트
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) { //Thread sleep 도중에 interrupted 발생
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null; // 락 획득 실패
    }

    public void unlock(String key, String lockValue) {
        String script = """
            if redis.call("get", KEYS[1]) == ARGV[1] then
                return redis.call("del", KEYS[1])
            else
                return 0
            end
        """;

        Long result = redisTemplate.execute(
                RedisScript.of(script, Long.class),
                Collections.singletonList(key),
                lockValue
        );

        boolean success = result != null && result > 0;
        if (!success) { //삭제 실패
            log.warn("Unlock failed: key={}, lockValue={}", key, lockValue);
        }
    }
}
