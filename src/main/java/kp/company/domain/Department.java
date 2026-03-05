package kp.company.domain;

import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

/**
 * The department.
 * <p>
 * A domain object to be persisted to Redis.
 * </p>
 *
 * @param name      the name of the department
 * @param employees the list of {@link Employee} objects
 */
public record Department(@Indexed String name, List<Employee> employees) {
}