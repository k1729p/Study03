package kp.company.configuration.operations;

import kp.company.domain.Department;
import kp.company.domain.Employee;
import kp.company.domain.Team;
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

/**
 * Creates Redis operations:
 * <ul>
 * <li>{@link ReactiveRedisOperations}</li>
 * <li>{@link ReactiveZSetOperations}</li>
 * </ul>
 */
@Configuration
public class RedisOperationsCreator {

    private static final RedisSerializer<String> KEY_SERIALIZER = new StringRedisSerializer();

    /**
     * Creates {@link ReactiveRedisOperations} for {@link Department} with {@link Employee}s.
     *
     * @param reactiveRedisConnectionFactory the {@link ReactiveRedisConnectionFactory}
     * @return the {@link ReactiveRedisOperations} for {@link Department} with {@link Employee}s
     */
    @Bean
    public ReactiveRedisOperations<String, Department> createRedisOperationsForDepartment(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, createDepartmentSerializationContext());
    }

    /**
     * Creates {@link ReactiveZSetOperations} for {@link Team}.
     *
     * @param reactiveRedisConnectionFactory the {@link ReactiveRedisConnectionFactory}
     * @return the {@link ReactiveZSetOperations} for {@link Team}
     */
    @Bean
    public ReactiveZSetOperations<String, Team> createRedisOperationsForTeam(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {

        final ReactiveRedisOperations<String, Team> reactiveRedisOperations = new ReactiveRedisTemplate<>(
                reactiveRedisConnectionFactory, createTeamSerializationContext());
        return reactiveRedisOperations.opsForZSet();
    }

    /**
     * Creates {@link RedisSerializationContext} for {@link Department}.
     *
     * @return the {@link RedisSerializationContext} for {@link Department}
     */
    private RedisSerializationContext<String, Department> createDepartmentSerializationContext() {

        final RedisSerializationContextBuilder<String, Department> serializationContextBuilder =
                RedisSerializationContext.newSerializationContext(KEY_SERIALIZER);
        final RedisSerializer<Department> departmentSerializer = new Jackson2JsonRedisSerializer<>(Department.class);
        return serializationContextBuilder.value(departmentSerializer).build();
    }

    /**
     * Creates {@link RedisSerializationContext} for {@link Team}.
     *
     * @return the {@link RedisSerializationContext} for {@link Team}
     */
    private RedisSerializationContext<String, Team> createTeamSerializationContext() {

        final RedisSerializationContextBuilder<String, Team> serializationContextBuilder =
                RedisSerializationContext.newSerializationContext(KEY_SERIALIZER);
        final RedisSerializer<Team> teamSerializer = new Jackson2JsonRedisSerializer<>(Team.class);
        return serializationContextBuilder.value(teamSerializer).build();
    }
}
