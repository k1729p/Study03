package kp.company.domain;

import java.util.List;

import org.springframework.data.redis.core.index.Indexed;

/**
 * The department.<br>
 * A domain object to be persisted to the Redis.
 * 
 * @param name      the name
 * @param employees the list of the {@link Employee}s
 */
public record Department(@Indexed String name, List<Employee> employees) {
}