package kp.company.handlers;

import static kp.Constants.NOT_FOUND_SUPPLIER;
import static kp.Constants.RANGE_FROM_VAR;
import static kp.Constants.RANGE_TO_VAR;
import static kp.Constants.TEAMS_KEY;
import static kp.Constants.TEAM_ID_VAR;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import kp.company.domain.Team;
import kp.company.domain.TeamTuple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The WebFlux handler for the {@link Team}s.<br/>
 * Uses Redis ZSet (sorted set) specific operations.
 *
 */
@Component
public class TeamHandler {
	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	private static final boolean VERBOSE = false;

	private final ReactiveZSetOperations<String, Team> reactiveZSetOperations;

	/**
	 * The constructor.
	 * 
	 * @param reactiveZSetOperations the {@link ReactiveZSetOperations} for the
	 *                               {@link Team}
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

		final Flux<TeamTuple> teamTupleFlux = reactiveZSetOperations.scan(TEAMS_KEY)
				.flatMap(tuple -> Mono.just(new TeamTuple(tuple.getValue(), tuple.getScore())));

		final Function<List<TeamTuple>, Mono<ServerResponse>> responseMapper = list -> list.isEmpty()
				? NOT_FOUND_SUPPLIER.get()
				: ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(list), List.class);

		final Mono<ServerResponse> serverResponseMono = teamTupleFlux.collectList().flatMap(responseMapper)/*-*/
				.transform(mono -> VERBOSE ? mono.log() : mono);
		logger.info("handleTeams():");
		return serverResponseMono;
	}

	/**
	 * Finds the range of {@link Team}s.<br/>
	 * Retrieve the top {@link Team}s by range.
	 * 
	 * @param request the {@link ServerRequest}
	 * @return the {@link ServerResponse} {@link Mono} with the {@link TeamTuple}s
	 */
	public Mono<ServerResponse> handleTeamsRangeByScore(ServerRequest request) {

		final double rangeFrom;
		final double rangeTo;
		try {
			rangeFrom = Double.valueOf(request.queryParam(RANGE_FROM_VAR).orElse(""));
			rangeTo = Double.valueOf(request.queryParam(RANGE_TO_VAR).orElse(""));
		} catch (Exception e) {
			logger.error(String.format("handleTeamsRangeByScore(): exception[%s]", e.getMessage()));
			return ServerResponse.badRequest().build();
		}
		final Flux<TeamTuple> teamTupleFlux = reactiveZSetOperations
				.rangeByScoreWithScores(TEAMS_KEY, Range.closed(rangeFrom, rangeTo))
				.flatMap(tuple -> Mono.just(new TeamTuple(tuple.getValue(), tuple.getScore())));

		final Function<List<TeamTuple>, Mono<ServerResponse>> responseMapper = list -> list.isEmpty()
				? NOT_FOUND_SUPPLIER.get()
				: ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(list), List.class);
		final Mono<ServerResponse> serverResponseMono = teamTupleFlux.collectList().flatMap(responseMapper)/*-*/
				.transform(mono -> VERBOSE ? mono.log() : mono);
		logger.info(String.format("handleTeamsRangeByScore(): rangeFrom[%.0f], rangeTo[%.0f]", rangeFrom, rangeTo));
		return serverResponseMono;
	}

	/**
	 * Finds the rank of the {@link Team}.<br/>
	 * Retrieve the rank of the {@link Team} by {@link Team} id.
	 * 
	 * @param request the {@link ServerRequest}
	 * @return the {@link ServerResponse} {@link Mono} with the rank
	 */
	public Mono<ServerResponse> handleTeamRankById(ServerRequest request) {

		final int teamId;
		try {
			teamId = Integer.valueOf(request.queryParam(TEAM_ID_VAR).orElse(""));
		} catch (Exception e) {
			logger.error(String.format("handleTeamRankById(): exception[%s]", e.getMessage()));
			return ServerResponse.badRequest().build();
		}
		final Mono<Long> rankMono = reactiveZSetOperations.rank(TEAMS_KEY, new Team(teamId));

		final Function<Long, Mono<ServerResponse>> responseMapper = rank -> ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON).body(Mono.just(rank), Long.class);

		final Mono<ServerResponse> serverResponseMono = rankMono.flatMap(responseMapper)
				.switchIfEmpty(NOT_FOUND_SUPPLIER.get())/*-*/
				.transform(mono -> VERBOSE ? mono.log() : mono);
		logger.info(String.format("handleTeamRankById(): teamId[%d]", teamId));
		return serverResponseMono;
	}

}
