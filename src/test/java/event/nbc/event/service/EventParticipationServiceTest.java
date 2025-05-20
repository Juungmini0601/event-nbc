package event.nbc.event.service;

import event.nbc.event.exception.EventException;
import event.nbc.event.exception.EventExceptionCode;
import event.nbc.event.repository.EventRedisRepository;
import event.nbc.redis.RedisLockService;
import org.junit.jupiter.api.BeforeEach;
import event.nbc.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "spring.profiles.active=test")
class EventParticipationServiceTest {

    @Autowired
    private EventParticipationService participationService;

    @Autowired
    private EventRedisRepository eventRepository;

    @Autowired
    private RandomGenerator randomGenerator;

    @Autowired
    private RedisLockService lockService;

    @BeforeEach
    void setUp() {
        // 초기 이벤트 데이터 세팅 (당첨 수량 10개)
        Event event = new Event(
                1L,
                5,
                List.of("success1.png", "success2.png", "success3.png", "success4.png", "success5.png"),
                LocalDateTime.now(), // 시작 시각
                LocalDateTime.now().plusMinutes(10)  // 종료 시각
        );
        eventRepository.save(event);
    }

    @Test
    void 동시에_n00명이_이벤트에_참여하면_락_획득_여부_확인() throws InterruptedException {
        int threadCount = 2000;
        ExecutorService executorService = Executors.newFixedThreadPool(200); // 스프링부트 기본 스레드값
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCnt = new AtomicInteger();
        AtomicInteger failCnt = new AtomicInteger();

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            executorService.execute(() -> {
                try{
                    String result  = participationService.participateEvent(1L);
                        successCnt.getAndIncrement();
                        //System.out.println("[Thread " + threadNum + "] 성공: " + result);
                    } catch (EventException e) {
                        if (e.getErrorCode() == EventExceptionCode.LOCK_FAILED) {
                            failCnt.getAndIncrement();
                            //System.out.println("[Thread " + threadNum + "] 락 획득 실패");
                        } else {
                            //System.out.println("[Thread " + threadNum + "] 예외 발생: " + e.getMessage());
                        }
                    } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 작업 완료 대기
        executorService.shutdown();
        System.out.println("== 모든 스레드 작업 종료 ==");
        System.out.println("락 획득 성공 스레드 " + successCnt.get());
        System.out.println("락 획득 실패 스레드 " + failCnt.get());

        long end = System.currentTimeMillis();
        System.out.println("⏱ 걸린 시간: " + (end - start) + "ms");
    }
}