package kp.company.handlers;

import kp.company.domain.Team;
import kp.company.domain.TeamTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static kp.Constants.*;

/**
 * The WebFlux handler for the {@link Team}s.
 * <p>
 * Uses Redis ZSet (sorted set) specific operations.
 * </p>
 */
@Component
public class TeamHandler {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final boolean VERBOSE = false;

    private final ReactiveZSetOperations<String, Team> reactiveZSetOperations;

    /**
     * Constructor.
     *
     * @param reactiveZSetOperations the {@link ReactiveZSetOperations} for the {@link Team}
     */
    public TeamHandler(ReactiveZSetOperations<String, Team> reactiveZSetOperations) {
        this.reactiveZSetOperations = reactiveZSetOperations;
    }

    /**
     * Finds all {@link Team}s.
     *
     * @param request the {@link ServerRequest}
     * @return the {@link ServerResponse} {@link Mono} with the {@link TeamTuple}s
     */
    public Mono<ServerResponse> handleTeams(ServerRequest request) {

        logger.debug("handleTeams(): request without query parameters[{}]", request.queryParams().isEmpty());
        final Flux<TeamTuple> teamTupleFlux = reactiveZSetOperations.scan(TEAMS_KEY).flatMap(
                tuple -> Mono.just(new TeamTuple(
                        tuple.getValue(), Optional.ofNullable(tuple.getScore()).orElse(0d))));

        final Function<List<TeamTuple>, Mono<ServerResponse>> responseMapper = list -> list.isEmpty()
                ? NOT_FOUND_SUPPLIER.get()
                : ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list);

        final Mono<ServerResponse> serverResponseMono = teamTupleFlux.collectList().flatMap(responseMapper)
                .transform(mono -> VERBOSE ? mono.log() : mono);
        logger.info("handleTeams():");
        return serverResponseMono;
    }

    /**
     * Finds the range of {@link Team}s.
     * <p>
     * Retrieve the top {@link Team}s by range.
     * </p>
     *
     * @param request the {@link ServerRequest}
     * @return the {@link ServerResponse} {@link Mono} with the {@link TeamTuple}s
     */
    public Mono<ServerResponse> handleTeamsRangeByScore(ServerRequest request) {

        final double rangeFrom;
        final double rangeTo;
        try {
            rangeFrom = Double.parseDouble(request.queryParam(RANGE_FROM_VAR).orElse(""));
            rangeTo = Double.parseDouble(request.queryParam(RANGE_TO_VAR).orElse(""));
        } catch (Exception e) {
            logger.error("handleTeamsRangeByScore(): exception[{}]", e.getMessage());
            return ServerResponse.badRequest().build();
        }
        final Flux<TeamTuple> teamTupleFlux = reactiveZSetOperations
                .rangeByScoreWithScores(TEAMS_KEY, Range.closed(rangeFrom, rangeTo))
                .flatMap(tuple -> Mono.just(
                        new TeamTuple(tuple.getValue(), Optional.ofNullable(tuple.getScore()).orElse(0d))));

        final Function<List<TeamTuple>, Mono<ServerResponse>> responseMapper = list -> list.isEmpty()
                ? NOT_FOUND_SUPPLIER.get()
                : ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list);
        final Mono<ServerResponse> serverResponseMono = teamTupleFlux.collectList().flatMap(responseMapper)
                .transform(mono -> VERBOSE ? mono.log() : mono);
        if (logger.isInfoEnabled()) {
            logger.info("handleTeamsRangeByScore(): rangeFrom[{}], rangeTo[{}]",
                    DBL_FMT.apply(rangeFrom), DBL_FMT.apply(rangeTo));
        }
        return serverResponseMono;
    }

    /**
     * Finds the rank of the {@link Team}.
     * <p>
     * Retrieve the rank of the {@link Team} by {@link Team} id.
     * </p>
     *
     * @param request the {@link ServerRequest}
     * @return the {@link ServerResponse} {@link Mono} with the rank
     */
    public Mono<ServerResponse> handleTeamRankById(ServerRequest request) {

        final int teamId;
        try {
            teamId = Integer.parseInt(request.queryParam(TEAM_ID_VAR).orElse(""));
        } catch (Exception e) {
            logger.error("handleTeamRankById(): exception[{}]", e.getMessage());
            return ServerResponse.badRequest().build();
        }
        final Mono<Long> rankMono = reactiveZSetOperations.rank(TEAMS_KEY, new Team(teamId));

        final Function<Long, Mono<ServerResponse>> responseMapper = rank -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON).bodyValue(rank);

        final Mono<ServerResponse> serverResponseMono = rankMono.flatMap(responseMapper)
                .switchIfEmpty(NOT_FOUND_SUPPLIER.get())
                .transform(mono -> VERBOSE ? mono.log() : mono);
        logger.info("handleTeamRankById(): teamId[{}]", teamId);
        return serverResponseMono;
    }

}
