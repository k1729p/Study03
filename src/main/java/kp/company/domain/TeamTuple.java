package kp.company.domain;

/**
 * The wrapper for the {@link Team} and its score.
 * 
 * @param team  the {@link Team}
 * @param score the score
 */
public record TeamTuple(Team team, double score) {
}