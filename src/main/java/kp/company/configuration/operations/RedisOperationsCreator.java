package kp.company.configuration.operations;

import kp.company.domain.Department;
import kp.company.domain.Employee;
import kp.company.domain.Team;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
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

    private static final RedisSerializer<@NonNull String> KEY_SERIALIZER = new StringRedisSerializer();

    /**
     * Creates {@link ReactiveRedisOperations} for {@link Department} with {@link Employee}s.
     *
     * @param reactiveRedisConnectionFactory the {@link ReactiveRedisConnectionFactory}
     * @return the {@link ReactiveRedisOperations} for {@link Department} with {@link Employee}s
     */
    @Bean
    public ReactiveRedisOperations<@NonNull String, @NonNull Department> createRedisOperationsForDepartment(
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

        final ReactiveRedisOperations<@NonNull String, @NonNull Team> reactiveRedisOperations = new ReactiveRedisTemplate<>(
                reactiveRedisConnectionFactory, createTeamSerializationContext());
        return reactiveRedisOperations.opsForZSet();
    }

    /**
     * Creates {@link RedisSerializationContext} for {@link Department}.
     *
     * @return the {@link RedisSerializationContext} for {@link Department}
     */
    private RedisSerializationContext<@NonNull String, @NonNull Department> createDepartmentSerializationContext() {

        final RedisSerializationContextBuilder<@NonNull String, @NonNull Department> serializationContextBuilder =
                RedisSerializationContext.newSerializationContext(KEY_SERIALIZER);
        final RedisSerializer<@NonNull Department> departmentSerializer = new JacksonJsonRedisSerializer<>(Department.class);
        return serializationContextBuilder.value(departmentSerializer).build();
    }

    /**
     * Creates {@link RedisSerializationContext} for {@link Team}.
     *
     * @return the {@link RedisSerializationContext} for {@link Team}
     */
    private RedisSerializationContext<@NonNull String, @NonNull Team> createTeamSerializationContext() {

        final RedisSerializationContextBuilder<@NonNull String, @NonNull Team> serializationContextBuilder =
                RedisSerializationContext.newSerializationContext(KEY_SERIALIZER);
        final RedisSerializer<@NonNull Team> teamSerializer = new JacksonJsonRedisSerializer<>(Team.class);
        return serializationContextBuilder.value(teamSerializer).build();
    }
}
