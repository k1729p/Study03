package kp.company.configuration.operations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import kp.company.domain.Department;
import kp.company.domain.Employee;
import kp.company.domain.Team;

/**
 * The creator for the Redis operations<br/>
 * (the {@link ReactiveRedisOperations} and the {@link ReactiveZSetOperations}).
 * 
 */
@Configuration
public class RedisOperationsCreator {

	private static final RedisSerializer<String> KEY_SERIALIZER = new StringRedisSerializer();

	/**
	 * The constructor.
	 */
	public RedisOperationsCreator() {
		super();
	}

	/**
	 * Creates the {@link ReactiveRedisTemplate} for the {@link Department} with the
	 * {@link Employee}s.
	 * 
	 * @param reactiveRedisConnectionFactory the
	 *                                       {@link ReactiveRedisConnectionFactory}
	 * @return the {@link ReactiveRedisOperations} for the {@link Department} with
	 *         {@link Employee}s
	 */
	@Bean
	public ReactiveRedisOperations<String, Department> createRedisOperationsForDepartment(
			ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		final RedisSerializationContextBuilder<String, Department> serializationContextBuilder = RedisSerializationContext
				.newSerializationContext(KEY_SERIALIZER);
		final RedisSerializer<Department> valueSerializer = new Jackson2JsonRedisSerializer<>(Department.class);
		final RedisSerializationContext<String, Department> serializationContext = serializationContextBuilder
				.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
	}

	/**
	 * Creates the reactive Redis template for the team.
	 * 
	 * @param reactiveRedisConnectionFactory the
	 *                                       {@link ReactiveRedisConnectionFactory}
	 * @return the {@link ReactiveZSetOperations} for the {@link Team}
	 */
	@Bean
	public ReactiveZSetOperations<String, Team> createRedisOperationsForTeam(
			ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {

		final RedisSerializationContextBuilder<String, Team> serializationContextBuilder = RedisSerializationContext
				.newSerializationContext(KEY_SERIALIZER);
		final RedisSerializer<Team> valueSerializer = new Jackson2JsonRedisSerializer<>(Team.class);
		final RedisSerializationContext<String, Team> serializationContext = serializationContextBuilder
				.value(valueSerializer).build();
		final ReactiveRedisOperations<String, Team> reactiveRedisOperations = new ReactiveRedisTemplate<>(
				reactiveRedisConnectionFactory, serializationContext);
		return reactiveRedisOperations.opsForZSet();
	}
}
