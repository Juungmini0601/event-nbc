package event.nbc.aop;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import event.nbc.event.exception.EventException;
import event.nbc.event.exception.EventExceptionCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Aspect
@Component
public class DistributedLockAspect {

	private final RedissonClient redissonClient;

	private final ExpressionParser parser = new SpelExpressionParser();
	private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

	@Around("@annotation(distributedLock)")
	public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
		String lockKey = parseKey(distributedLock.key(), joinPoint);
		if (lockKey == null) {
			throw new EventException(EventExceptionCode.LOCK_FAILED);
		}
		RLock lock = redissonClient.getLock(lockKey);

		boolean locked = false;
		try {
			locked = lock.tryLock(
				distributedLock.waitTime(),
				distributedLock.leaseTime(),
				TimeUnit.SECONDS
			);
			if (!locked) {
				throw new EventException(EventExceptionCode.LOCK_FAILED);
			}

			return joinPoint.proceed();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new EventException(EventExceptionCode.LOCK_FAILED);
		} finally {
			if (locked && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	private String parseKey(String spelKey, ProceedingJoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();

		Object[] args = joinPoint.getArgs();
		String[] paramNames = nameDiscoverer.getParameterNames(method);
		if (paramNames == null || paramNames.length == 0) {
			return spelKey;
		}

		StandardEvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < paramNames.length; i++) {
			context.setVariable(paramNames[i], args[i]);
		}

		try {
			Expression expression = parser.parseExpression(spelKey);
			return expression.getValue(context, String.class);
		} catch (Exception e) {
			throw new IllegalArgumentException("잘못된 SpEL 표현식입니다: " + spelKey, e);
		}
	}
}
