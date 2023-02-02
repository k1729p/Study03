package kp.company.handlers;

import static kp.Constants.GET_TEAMS_PATH;
import static kp.Constants.GET_TEAMS_RANGE_PATH;
import static kp.Constants.GET_TEAM_RANK_PATH;
import static kp.Constants.RANGE_FROM_VAR;
import static kp.Constants.RANGE_TO_VAR;
import static kp.Constants.TEAMS_KEY;
import static kp.Constants.TEAM_ID_VAR;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;

import kp.company.domain.Team;
import kp.company.domain.TeamTuple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The {@link TeamHandler} tests.<br/>
 * The tests use {@link WebTestClient}.
 *
 */
@SpringBootTest
class TeamHandlerTests {
	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	/**
	 * The {@link RouterFunction}.
	 */
	@Autowired
	RouterFunction<ServerResponse> routerFunction;

	private WebTestClient webTestClient;

	@MockBean
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
	 * The constructor.
	 */
	TeamHandlerTests() {
		super();
	}

	/**
	 * Executed before each test.
	 * 
	 */
	@BeforeEach
	void setup() {
		webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
	}

	/**
	 * Should get the list of {@link Team}s.
	 * 
	 * @throws Exception the {@link Exception}
	 */
	@Test
	void shouldGetTeams() throws Exception {
		// GIVEN
		Mockito.when(reactiveZSetOperations.scan(TEAMS_KEY)).thenReturn(MOCK_TUPLE_FLUX);
		// WHEN
		final ResponseSpec responseSpec = webTestClient.get().uri(GET_TEAMS_PATH).accept(MediaType.APPLICATION_JSON)
				.exchange();
		// THEN
		responseSpec.expectStatus().isOk();
		responseSpec.expectBodyList(TeamTuple.class).value(list -> checkTeamTuples(EXPECTED_TEAM_TUPLE_LIST, list));
		logger.debug("shouldGetTeams():");
	}

	/**
	 * Should get the range of {@link Team}s by score.
	 * 
	 * @throws Exception the {@link Exception}
	 */
	@Test
	void shouldGetTeamsRangeByScore() throws Exception {
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
		logger.debug("shouldGetTeamsRangeByScore():");
	}

	/**
	 * Should get the rank of {@link Team} by {@link Team} id.
	 * 
	 * @throws Exception the {@link Exception}
	 */
	@Test
	void shouldGetTeamRankById() throws Exception {
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
		logger.debug("shouldGetTeamRankById():");
	}

	/**
	 * Should not get the list of {@link Team}s and get status 'Not Found'.
	 * 
	 * @throws Exception the {@link Exception}
	 */
	@Test
	void shouldNotGetTeamsAndGetStatusNotFound() throws Exception {
		// GIVEN
		Mockito.when(reactiveZSetOperations.scan(TEAMS_KEY)).thenReturn(Flux.empty());
		// WHEN
		final ResponseSpec responseSpec = webTestClient.get().uri(GET_TEAMS_PATH).accept(MediaType.APPLICATION_JSON)
				.exchange();
		// THEN
		responseSpec.expectStatus().isNotFound();
		logger.debug("shouldNotGetTeamsAndGetStatusNotFound():");
	}

	/**
	 * Should not get the {@link Team}s range by the unknown score and get status
	 * 'Not Found'.
	 * 
	 * @throws Exception the {@link Exception}
	 */
	@Test
	void shouldNotGetTeamsRangeByUnknownScoreAndGetStatusNotFound() throws Exception {
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
		logger.debug("shouldNotGetTeamsRangeByUnknownScoreAndGetStatusNotFound():");
	}

	/**
	 * Should not get the {@link Team}s range by the bad score and get status 'Bad
	 * Request'.
	 * 
	 * @throws Exception the {@link Exception}
	 */
	@Test
	void shouldNotGetTeamsRangeByBadScoreAndGetStatusBadRequest() throws Exception {
		// GIVEN
		// WHEN
		final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAMS_RANGE_PATH)
				.queryParam(RANGE_FROM_VAR, TEST_SCORE_1).queryParam(RANGE_TO_VAR, TEST_SCORE_BAD).build();
		final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
				.exchange();
		// THEN
		responseSpec.expectStatus().isBadRequest();
		logger.debug("shouldNotGetTeamsRangeByBadScoreAndGetStatusBadRequest():");
	}

	/**
	 * Should not get the rank of the {@link Team} by the unknown id and get status
	 * 'Not Found'.
	 * 
	 * @throws Exception the {@link Exception}
	 */
	@Test
	void shouldNotGetTeamRankByUnknownIdAndGetStatusNotFound() throws Exception {
		// GIVEN
		Mockito.when(reactiveZSetOperations.rank(TEAMS_KEY, TEST_TEAM_UNKNOWN)).thenReturn(Mono.empty());
		// WHEN
		final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAM_RANK_PATH)
				.queryParam(TEAM_ID_VAR, TEST_TEAM_UNKNOWN.id()).build();
		final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
				.exchange();
		// THEN
		responseSpec.expectStatus().isNotFound();
		logger.debug("shouldNotGetTeamRankByUnknownIdAndGetStatusNotFound():");
	}

	/**
	 * Should not get the rank of the {@link Team} by the bad id and get status 'Bad
	 * Request'.
	 * 
	 * @throws Exception the {@link Exception}
	 */
	@Test
	void shouldNotGetTeamRankByBadIdAndGetStatusBadRequest() throws Exception {
		// GIVEN
		// WHEN
		final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_TEAM_RANK_PATH)
				.queryParam(TEAM_ID_VAR, TEST_TEAM_ID_BAD).build();
		final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
				.exchange();
		// THEN
		responseSpec.expectStatus().isBadRequest();
		logger.debug("shouldNotGetTeamRankByBadIdAndGetStatusBadRequest():");
	}

	/**
	 * Checks the {@link TeamTuple} list.
	 * 
	 * @param expectedTeamList the expected {@link TeamTuple} list
	 * @param actualTeamList   the actual {@link TeamTuple} list
	 */
	private void checkTeamTuples(List<TeamTuple> expectedTeamTupleList, List<TeamTuple> actualTeamTupleList) {

		Assertions.assertNotNull(actualTeamTupleList, TEAM_TUPLE_LIST_NULL_ERR_MSG);
		Assertions.assertEquals(expectedTeamTupleList.size(), expectedTeamTupleList.size(),
				TEAM_TUPLE_LIST_SIZE_ERR_MSG);
		IntStream.range(0, expectedTeamTupleList.size()).forEach(i -> {
			Assertions.assertEquals(expectedTeamTupleList.get(i).team().id(), actualTeamTupleList.get(i).team().id(),
					TEAM_ID_ERR_MSG);
			Assertions.assertEquals(expectedTeamTupleList.get(i).score(), actualTeamTupleList.get(i).score(),
					SCORE_ERR_MSG);
		});
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
