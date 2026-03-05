package kp.company.domain;

import org.springframework.data.annotation.Id;

/**
 * Represents a team.
 * <p>
 * A domain object to be persisted to Redis.
 * </p>
 *
 * @param id the unique identifier of the team
 */
public record Team(@Id int id) {
}