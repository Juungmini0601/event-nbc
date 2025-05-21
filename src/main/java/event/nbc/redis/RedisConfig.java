package event.nbc.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import event.nbc.model.Event;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<Long, Event> EventRedisTemplate(
		RedisConnectionFactory factory,
		ObjectMapper objectMapper
	) {
		RedisTemplate<Long, Event> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);

		GenericToStringSerializer<Long> keySerializer =
			new GenericToStringSerializer<>(Long.class);

		template.setKeySerializer(keySerializer);

		JavaType eventType = objectMapper.getTypeFactory()
			.constructType(Event.class);

		Jackson2JsonRedisSerializer<Event> valueSerializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, eventType);

		template.setValueSerializer(valueSerializer);

		return template;
	}
}
