package kp.client.subscribers;

import kp.company.domain.TeamTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.concurrent.Phaser;
import java.util.function.Consumer;
import java.util.function.Function;

import static kp.Constants.*;

/**
 * The subscriber for the {@link kp.company.domain.Team}s.
 */
public class TeamSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final boolean VERBOSE = false;

    private final WebClient client;

    /**
     * Constructor.
     *
     * @param client the {@link WebClient}
     */
    public TeamSubscriber(WebClient client) {
        this.client = client;
    }

    /**
     * Subscribes to all {@link kp.company.domain.Team}s.
     */
    public void subscribeTeams() {

        final Flux<TeamTuple> teamsFlux = client.get().uri(GET_TEAMS_PATH).retrieve().bodyToFlux(TeamTuple.class)
                .transform(flux -> VERBOSE ? flux.log() : flux);

        final Phaser phaser = new Phaser(1);
        final Consumer<TeamTuple> nextConsumer = tuple -> {
            if (logger.isInfoEnabled()) {
                logger.info("subscribeTeams(): team id[{}], score[{}]", tuple.team().id(), DBL_FMT.apply(tuple.score()));
            }
        };
        final Consumer<Throwable> errorConsumer = exc -> {
            logger.error("subscribeTeams(): exception[{}]", exc.getMessage());
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
     * Subscribes to a range of {@link kp.company.domain.Team}s by score.
     *
     * @param rangeFrom the start range
     * @param rangeTo   the end range
     */
    public void subscribeTeamsRangeByScore(String rangeFrom, String rangeTo) {

        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAMS_RANGE_PATH)
                .queryParam(RANGE_FROM_VAR, rangeFrom).queryParam(RANGE_TO_VAR, rangeTo).build();

        final Flux<TeamTuple> teamsFlux = client.get().uri(uriFunction).retrieve().bodyToFlux(TeamTuple.class)
                .transform(flux -> VERBOSE ? flux.log() : flux);

        final Phaser phaser = new Phaser(1);
        final Consumer<TeamTuple> nextConsumer = tuple -> {
            if (logger.isInfoEnabled()) {
                logger.info("subscribeTeamsRangeByScore(): team id[{}], score[{}]", tuple.team().id(), DBL_FMT.apply(tuple.score()));
            }
        };
        final Consumer<Throwable> errorConsumer = exc -> {
            logger.error("subscribeTeamsRangeByScore(): exception[{}]", exc.getMessage());
            phaser.forceTermination();
        };
        final Runnable completeConsumer = () -> {
            logger.info("subscribeTeamsRangeByScore(): completed, rangeFrom[{}], rangeTo[{}]", rangeFrom, rangeTo);
            phaser.arriveAndDeregister();
        };
        phaser.register();
        teamsFlux.subscribe(nextConsumer, errorConsumer, completeConsumer);
        phaser.arriveAndAwaitAdvance();
    }

    /**
     * Subscribes to {@link kp.company.domain.Team}s rank by id using merged {@link Mono}.
     *
     * @param teamId1 the first team id
     * @param teamId2 the second team id
     * @param teamId3 the third team id
     */
    public void subscribeZippedTeamRankById(String teamId1, String teamId2, String teamId3) {

        final Mono<Tuple3<Long, Long, Long>> tuple3Mono = Mono.zip(prepareRankMono(teamId1), prepareRankMono(teamId2),
                prepareRankMono(teamId3));
        tuple3Mono.blockOptional().ifPresentOrElse(
                tuple3 -> logger.info("subscribeZippedTeamRankById():\n"
                                      + "\tteam 1 -> id[{}], rank[{}]; team 2 -> id[{}], rank[{}]; team 3 -> id[{}], rank[{}]",
                        teamId1, tuple3.getT1(), teamId2, tuple3.getT2(), teamId3, tuple3.getT3()),
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
        return client.get().uri(uriFunction).retrieve().bodyToMono(Long.class)
                .transform(mono -> VERBOSE ? mono.log() : mono);
    }

}
