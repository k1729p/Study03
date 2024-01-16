package kp.company.domain;

import org.springframework.data.annotation.Id;

/**
 * The team.<br>
 * A domain object to be persisted to the Redis.
 * 
 * @param id the id
 */
public record Team(@Id int id) {
}