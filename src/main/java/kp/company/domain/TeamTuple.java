package kp.company.domain;

/**
 * A wrapper for the {@link Team} and its score.
 * <p>
 * This object is <b>NOT</b> persisted in Redis.
 * </p>
 *
 * @param team  the {@link Team}
 * @param score the score of the team
 */
public record TeamTuple(Team team, double score) {
}