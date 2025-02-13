package kp.company.handlers;

import kp.company.domain.Team;
import kp.company.domain.TeamTuple;
import kp.company.handlers.base.HandlersTestsBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static kp.Constants.*;

/**
 * The {@link TeamHandler} tests.
 * <p>
 * The tests use {@link WebTestClient}.
 * </p>
 * <p>
 * This test is designed to be run as an integration test (not as a unit test), hence the use of {@link MockitoBean}.
 * </p>
 */
class TeamHandlerTests extends HandlersTestsBase {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @MockitoBean
    private ReactiveZSetOperations<String, Team> reactiveZSetOperations;

    private static final Team TEST_TEAM_1 = new Team(1);
    private static final Team TEST_TEAM_2 = new Team(2);
    private static final Team TEST_TEAM_3 = new Team(3);
    private static final Team TEST_TEAM_4 = new Team(4);
    private static final Team TEST_TEAM_UNKNOWN = new Team(123);
    private static final double TEST_SCORE_1 = 1d;
    private static final double TEST_SCORE_2 = 2d;
    private static final double TEST_SCORE_3 = 3d;
    private static final double TEST_SCORE_4 = 4d;
    private static final double TEST_SCORE_UNKNOWN_1 = 123d;
    private static final double TEST_SCORE_UNKNOWN_2 = 456d;
    private static final String TEST_SCORE_BAD = "ABC";
    private static final String TEST_TEAM_ID_BAD = "ABC";

    private static final Range<Double> TEST_TEAMS_RANGE = Range.closed(TEST_SCORE_2, TEST_SCORE_3);
    private static final long TEST_TEAM_RANK = 3L;

    private static final Flux<TypedTuple<Team>> MOCK_TUPLE_FLUX = Flux.just(TypedTuple.of(TEST_TEAM_4, TEST_SCORE_1),
            TypedTuple.of(TEST_TEAM_3, TEST_SCORE_2), TypedTuple.of(TEST_TEAM_2, TEST_SCORE_3),
            TypedTuple.of(TEST_TEAM_1, TEST_SCORE_4));
    private static final Flux<TypedTuple<Team>> MOCK_TUPLE_RANGED_FLUX = Flux
            .just(TypedTuple.of(TEST_TEAM_3, TEST_SCORE_2), TypedTuple.of(TEST_TEAM_2, TEST_SCORE_3));

    private static final List<TeamTuple> EXPECTED_TEAM_TUPLE_LIST = List.of(new TeamTuple(TEST_TEAM_4, TEST_SCORE_1),
            new TeamTuple(TEST_TEAM_3, TEST_SCORE_2), new TeamTuple(TEST_TEAM_2, TEST_SCORE_3),
            new TeamTuple(TEST_TEAM_1, TEST_SCORE_4));
    private static final List<TeamTuple> EXPECTED_TEAM_TUPLE_RANGED_LIST = List
            .of(new TeamTuple(TEST_TEAM_3, TEST_SCORE_2), new TeamTuple(TEST_TEAM_2, TEST_SCORE_3));

    private static final String TEAM_TUPLE_LIST_NULL_ERR_MSG = "List of team tuples is null";
    private static final String RANK_NULL_ERR_MSG = "Rank is null";
    private static final String TEAM_TUPLE_LIST_SIZE_ERR_MSG = "Bad team tuple list size";
    private static final String TEAM_ID_ERR_MSG = "Bad id of the team";
    private static final String SCORE_ERR_MSG = "Bad score";
    private static final String TEAM_RANK_ERR_MSG = "Bad rank of the team";

    /**
     * Should get the list of {@link Team}s.
     */
    @Test
    void shouldGetTeams() {
        // GIVEN
        Mockito.when(reactiveZSetOperations.scan(TEAMS_KEY)).thenReturn(MOCK_TUPLE_FLUX);
        // WHEN
        final ResponseSpec responseSpec = webTestClient.get().uri(GET_TEAMS_PATH).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isOk();
        responseSpec.expectBodyList(TeamTuple.class)
                .value(list -> checkTeamTuples(EXPECTED_TEAM_TUPLE_LIST, list));
        logger.info("shouldGetTeams():");
    }

    /**
     * Should get the range of {@link Team}s by score.
     */
    @Test
    void shouldGetTeamsRangeByScore() {
        // GIVEN
        Mockito.when(reactiveZSetOperations.rangeByScoreWithScores(TEAMS_KEY, TEST_TEAMS_RANGE))
                .thenReturn(MOCK_TUPLE_RANGED_FLUX);
        // WHEN
        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAMS_RANGE_PATH)
                .queryParam(RANGE_FROM_VAR, TEST_SCORE_2).queryParam(RANGE_TO_VAR, TEST_SCORE_3).build();
        final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isOk();
        responseSpec.expectBodyList(TeamTuple.class)
                .value(list -> checkTeamTuples(EXPECTED_TEAM_TUPLE_RANGED_LIST, list));
        logger.info("shouldGetTeamsRangeByScore():");
    }

    /**
     * Should get the rank of {@link Team} by {@link Team} id.
     */
    @Test
    void shouldGetTeamRankById() {
        // GIVEN
        Mockito.when(reactiveZSetOperations.rank(TEAMS_KEY, TEST_TEAM_1)).thenReturn(Mono.just(TEST_TEAM_RANK));
        // WHEN
        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAM_RANK_PATH)
                .queryParam(TEAM_ID_VAR, TEST_TEAM_1.id()).build();
        final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isOk();
        responseSpec.expectBody(Long.class).value(this::checkRank);
        logger.info("shouldGetTeamRankById():");
    }

    /**
     * Should not get the list of {@link Team}s and get status 'Not Found'.
     */
    @Test
    void shouldNotGetTeamsAndGetStatusNotFound() {
        // GIVEN
        Mockito.when(reactiveZSetOperations.scan(TEAMS_KEY)).thenReturn(Flux.empty());
        // WHEN
        final ResponseSpec responseSpec = webTestClient.get().uri(GET_TEAMS_PATH).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isNotFound();
        logger.info("shouldNotGetTeamsAndGetStatusNotFound():");
    }

    /**
     * Should not get the {@link Team}s range by the unknown score and get status 'Not Found'.
     */
    @Test
    void shouldNotGetTeamsRangeByUnknownScoreAndGetStatusNotFound() {
        // GIVEN
        final Range<Double> range = Range.closed(TEST_SCORE_UNKNOWN_1, TEST_SCORE_UNKNOWN_2);
        Mockito.when(reactiveZSetOperations.rangeByScoreWithScores(TEAMS_KEY, range)).thenReturn(Flux.empty());
        // WHEN
        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAMS_RANGE_PATH)
                .queryParam(RANGE_FROM_VAR, TEST_SCORE_UNKNOWN_1).queryParam(RANGE_TO_VAR, TEST_SCORE_UNKNOWN_2)
                .build();
        final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isNotFound();
        logger.info("shouldNotGetTeamsRangeByUnknownScoreAndGetStatusNotFound():");
    }

    /**
     * Should not get the {@link Team}s range by the bad score and get status 'Bad Request'.
     */
    @Test
    void shouldNotGetTeamsRangeByBadScoreAndGetStatusBadRequest() {
        // GIVEN
        // WHEN
        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAMS_RANGE_PATH)
                .queryParam(RANGE_FROM_VAR, TEST_SCORE_1).queryParam(RANGE_TO_VAR, TEST_SCORE_BAD).build();
        final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isBadRequest();
        logger.info("shouldNotGetTeamsRangeByBadScoreAndGetStatusBadRequest():");
    }

    /**
     * Should not get the rank of the {@link Team} by the unknown id and get status 'Not Found'.
     */
    @Test
    void shouldNotGetTeamRankByUnknownIdAndGetStatusNotFound() {
        // GIVEN
        Mockito.when(reactiveZSetOperations.rank(TEAMS_KEY, TEST_TEAM_UNKNOWN)).thenReturn(Mono.empty());
        // WHEN
        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAM_RANK_PATH)
                .queryParam(TEAM_ID_VAR, TEST_TEAM_UNKNOWN.id()).build();
        final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isNotFound();
        logger.info("shouldNotGetTeamRankByUnknownIdAndGetStatusNotFound():");
    }

    /**
     * Should not get the rank of the {@link Team} by the bad id and get status 'Bad Request'.
     */
    @Test
    void shouldNotGetTeamRankByBadIdAndGetStatusBadRequest() {
        // GIVEN
        // WHEN
        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAM_RANK_PATH)
                .queryParam(TEAM_ID_VAR, TEST_TEAM_ID_BAD).build();
        final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isBadRequest();
        logger.info("shouldNotGetTeamRankByBadIdAndGetStatusBadRequest():");
    }

    /**
     * Checks the {@link TeamTuple} list.
     *
     * @param expectedTeamTupleList the expected {@link TeamTuple} list
     * @param actualTeamTupleList   the actual {@link TeamTuple} list
     */
    private void checkTeamTuples(List<TeamTuple> expectedTeamTupleList, List<TeamTuple> actualTeamTupleList) {

        Assertions.assertNotNull(actualTeamTupleList, TEAM_TUPLE_LIST_NULL_ERR_MSG);
        final int expectedSize = expectedTeamTupleList.size();
        Assertions.assertEquals(expectedSize, actualTeamTupleList.size(), TEAM_TUPLE_LIST_SIZE_ERR_MSG);
        IntStream.range(0, expectedSize)
                .forEach(i -> checkTeamTuple(expectedTeamTupleList.get(i), actualTeamTupleList.get(i)));
    }

    /**
     * Checks the {@link TeamTuple}
     *
     * @param expectedTeamTuple the expected {@link TeamTuple}
     * @param actualTeamTuple   the actual {@link TeamTuple}
     */
    private void checkTeamTuple(TeamTuple expectedTeamTuple, TeamTuple actualTeamTuple) {

        Assertions.assertEquals(expectedTeamTuple.team().id(), actualTeamTuple.team().id(),
                TEAM_ID_ERR_MSG);
        Assertions.assertEquals(expectedTeamTuple.score(), actualTeamTuple.score(),
                SCORE_ERR_MSG);
    }

    /**
     * Checks the rank of the {@link Team}.
     *
     * @param actualRank the actual rank of the {@link Team}
     */
    private void checkRank(Long actualRank) {
        Assertions.assertNotNull(actualRank, RANK_NULL_ERR_MSG);
        Assertions.assertEquals(TEST_TEAM_RANK, actualRank, TEAM_RANK_ERR_MSG);
    }

}
