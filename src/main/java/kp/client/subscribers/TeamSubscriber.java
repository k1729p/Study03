package kp.client.subscribers;

import static kp.Constants.GET_TEAMS_PATH;
import static kp.Constants.GET_TEAMS_RANGE_PATH;
import static kp.Constants.GET_TEAM_RANK_PATH;
import static kp.Constants.RANGE_FROM_VAR;
import static kp.Constants.RANGE_TO_VAR;
import static kp.Constants.TEAM_ID_VAR;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.concurrent.Phaser;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import kp.company.domain.TeamTuple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

/**
 * The subscriber for the teams.
 *
 */
public class TeamSubscriber {
	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	private static final boolean VERBOSE = false;

	private final WebClient client;

	/**
	 * The constructor.
	 * 
	 * @param client the {@link WebClient}
	 */
	public TeamSubscriber(WebClient client) {
		this.client = client;
	}

	/**
	 * Get all teams.
	 * 
	 */
	public void subscribeTeams() {

		final Flux<TeamTuple> teamsFlux = client.get().uri(GET_TEAMS_PATH).retrieve().bodyToFlux(TeamTuple.class)/*-*/
				.transform(flux -> VERBOSE ? flux.log() : flux);

		final Phaser phaser = new Phaser(1);
		final Consumer<TeamTuple> nextConsumer = tuple -> logger
				.info(String.format("subscribeTeams(): team id[%d], score[%.0f]", tuple.team().id(), tuple.score()));
		final Consumer<Throwable> errorConsumer = exc -> {
			logger.error(String.format("subscribeTeams(): exception[%s]", exc.getMessage()));
			phaser.forceTermination();
		};
		final Runnable completeConsumer = () -> {
			logger.debug("subscribeTeams(): completed");
			phaser.arriveAndDeregister();
		};
		phaser.register();
		teamsFlux.subscribe(nextConsumer, errorConsumer, completeConsumer);
		phaser.arriveAndAwaitAdvance();
	}

	/**
	 * Get teams range by score.
	 * 
	 * @param rangeFrom the start range
	 * @param rangeTo   the end range
	 */
	public void subscribeTeamsRangeByScore(String rangeFrom, String rangeTo) {

		final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAMS_RANGE_PATH)
				.queryParam(RANGE_FROM_VAR, rangeFrom).queryParam(RANGE_TO_VAR, rangeTo).build();

		final Flux<TeamTuple> teamsFlux = client.get().uri(uriFunction).retrieve().bodyToFlux(TeamTuple.class)/*-*/
				.transform(flux -> VERBOSE ? flux.log() : flux);

		final Phaser phaser = new Phaser(1);
		final Consumer<TeamTuple> nextConsumer = tuple -> logger.info(String
				.format("subscribeTeamsRangeByScore(): team id[%d], score[%.0f]", tuple.team().id(), tuple.score()));
		final Consumer<Throwable> errorConsumer = exc -> {
			logger.error(String.format("subscribeTeamsRangeByScore(): exception[%s]", exc.getMessage()));
			phaser.forceTermination();
		};
		final Runnable completeConsumer = () -> {
			logger.info(String.format("subscribeTeamsRangeByScore(): completed, rangeFrom[%s], rangeTo[%s]", rangeFrom,
					rangeTo));
			phaser.arriveAndDeregister();
		};
		phaser.register();
		teamsFlux.subscribe(nextConsumer, errorConsumer, completeConsumer);
		phaser.arriveAndAwaitAdvance();
	}

	/**
	 * Gets teams rank by id using merged {@link Mono}.
	 * 
	 * @param teamId1 the first team id
	 * @param teamId2 the second team id
	 * @param teamId3 the third team id
	 */
	public void subscribeZippedTeamRankById(String teamId1, String teamId2, String teamId3) {

		final Mono<Tuple3<Long, Long, Long>> tuple3Mono = Mono.zip(prepareRankMono(teamId1), prepareRankMono(teamId2),
				prepareRankMono(teamId3));
		tuple3Mono.blockOptional().ifPresentOrElse(
				tuple3 -> logger.info(String.format(
						"subscribeZippedTeamRankById():%n\t"
								+ "team 1 -> id[%s], rank[%d]; team 2 -> id[%s], rank[%d]; team 3 -> id[%s], rank[%d]",
						teamId1, tuple3.getT1(), teamId2, tuple3.getT2(), teamId3, tuple3.getT3())),
				() -> logger.info("subscribeZippedTeamRankById(): mono completed empty"));
	}

	/**
	 * Prepares team rank {@link Mono}.
	 * 
	 * @param teamId the team id
	 * @return the team rank {@link Mono}
	 */
	private Mono<Long> prepareRankMono(String teamId) {

		final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAM_RANK_PATH)
				.queryParam(TEAM_ID_VAR, teamId).build();
		return client.get().uri(uriFunction).retrieve().bodyToMono(Long.class)/*-*/
				.transform(flux -> VERBOSE ? flux.log() : flux);
	}

}
