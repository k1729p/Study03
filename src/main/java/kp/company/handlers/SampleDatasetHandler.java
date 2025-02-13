package kp.company.handlers;

import kp.company.domain.Department;
import kp.company.domain.Employee;
import kp.company.domain.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static kp.Constants.*;

/**
 * The WebFlux handler for the sample dataset loading.
 * <p>
 * The sample dataset for two {@link Department}s and four {@link Employee}s:
 * </p>
 * <ol>
 *  <li>{@link Department}
 *   <ol>
 *    <li>{@link Employee}</li>
 *    <li>{@link Employee}</li>
 *   </ol>
 *  </li>
 *  <li>{@link Department}
 *   <ol>
 *    <li>{@link Employee}</li>
 *    <li>{@link Employee}</li>
 *   </ol>
 *  </li>
 * </ol>
 * <p>
 * The sample dataset for five {@link Team}s:
 * </p>
 * <ol>
 *  <li>{@link Team}</li>
 *  <li>{@link Team}</li>
 *  <li>{@link Team}</li>
 *  <li>{@link Team}</li>
 *  <li>{@link Team}</li>
 * </ol>
 */
@Component
public class SampleDatasetHandler {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;
    private final ReactiveRedisOperations<String, Department> departmentRedisOperations;
    private final ReactiveZSetOperations<String, Team> teamRedisOperations;

    private static final IntFunction<List<Employee>> EMP_LIST_FUN = depIndex -> IntStream
            .rangeClosed(EMP_INDEX_LOWER_BOUND, EMP_INDEX_UPPER_BOUND).boxed()
            .map(empIndex -> EMP_INDEX_FUN.applyAsInt(depIndex, empIndex)).map(Employee::fromIndex).toList();

    private static final IntFunction<Map.Entry<String, Department>> MAP_ENTRY_FUN = depIndex -> Map.entry(
            DEP_KEY_FUN.apply(depIndex), new Department(DEP_NAME_FUN.apply(depIndex), EMP_LIST_FUN.apply(depIndex)));

    /**
     * The dataset map.
     */
    private static final Map<String, Department> DATASET_MAP = IntStream
            .rangeClosed(DEP_KEY_INDEX_LOWER_BOUND, DEP_KEY_INDEX_UPPER_BOUND).boxed().map(MAP_ENTRY_FUN::apply)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    /**
     * Constructor.
     *
     * @param reactiveRedisConnectionFactory the {@link ReactiveRedisConnectionFactory}
     * @param departmentRedisOperations      the {@link ReactiveRedisOperations} for the {@link Department}s
     * @param teamRedisOperations            the {@link ReactiveZSetOperations} for the {@link Team}s
     */
    public SampleDatasetHandler(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory,
                                ReactiveRedisOperations<String, Department> departmentRedisOperations,
                                ReactiveZSetOperations<String, Team> teamRedisOperations) {

        this.reactiveRedisConnectionFactory = reactiveRedisConnectionFactory;
        this.departmentRedisOperations = departmentRedisOperations;
        this.teamRedisOperations = teamRedisOperations;
    }

    /**
     * Handles the sample dataset loading.
     *
     * @param request the {@link ServerRequest}
     * @return the {@link ServerResponse} {@link Mono}
     */
    public Mono<ServerResponse> handleSampleDatasetLoading(ServerRequest request) {

        logger.debug("handleSampleDatasetLoading(): request without query parameters[{}]", request.queryParams().isEmpty());
        deleteAllKeysInDatabase();
        loadDepartmentData();
        loadTeamData();
        final Mono<ServerResponse> serverResponseMono = ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(LOAD_SAMPLE_DATASET_RESULT_JSON), String.class);
        logger.info("handleSampleDatasetLoading():");
        return serverResponseMono;
    }

    /**
     * Deletes all keys in database.
     */
    private void deleteAllKeysInDatabase() {

        final Phaser phaser = new Phaser(1);
        phaser.register();
        final Consumer<Throwable> errorConsumer = exc -> {
            logger.error("deleteAllKeysInDatabase(): deleting keys exception[{}]", exc.getMessage());
            phaser.forceTermination();
        };
        final Runnable completeConsumer = () -> {
            logger.debug("deleteAllKeysInDatabase(): deleting completed");
            phaser.arriveAndDeregister();
        };
        reactiveRedisConnectionFactory.getReactiveConnection().serverCommands().flushAll()
                .subscribe(null, errorConsumer, completeConsumer);
        phaser.arriveAndAwaitAdvance();
    }

    /**
     * Generates the data for {@link Department}s with {@link Employee}s
     */
    private void loadDepartmentData() {

        final Phaser phaser = new Phaser(1);
        final Consumer<Boolean> nextConsumer = result -> logger.debug("loadDepartmentData(): result[{}]", result);
        final Consumer<Throwable> errorConsumer = exc -> {
            logger.error("loadDepartmentData(): exception[{}]", exc.getMessage());
            phaser.forceTermination();
        };
        final Runnable completeConsumer = () -> {
            logger.debug("loadDepartmentData(): loading completed");
            phaser.arriveAndDeregister();
        };
        phaser.register();
        departmentRedisOperations.opsForValue().multiSet(DATASET_MAP).subscribe(nextConsumer, errorConsumer,
                completeConsumer);
        phaser.arriveAndAwaitAdvance();
    }

    /**
     * Generates the data for {@link Team}s.
     */
    private void loadTeamData() {

        final Phaser phaser = new Phaser(1);
        final AtomicInteger atomic = new AtomicInteger(TEAMS_COUNT);
        final Flux<Boolean> addFlux = Flux.range(TEAM_ID_LOWER_BOUND, TEAMS_COUNT).map(Team::new)
                .flatMap(team -> teamRedisOperations.add(TEAMS_KEY, team, atomic.getAndDecrement()));
        final Consumer<Boolean> nextConsumer = result -> logger.debug("loadTeamData(): result[{}]", result);
        final Consumer<Throwable> errorConsumer = exc -> {
            logger.error("loadTeamData(): exception[{}]", exc.getMessage());
            phaser.forceTermination();
        };
        final Runnable completeConsumer = () -> {
            logger.debug("loadTeamData(): loading completed");
            phaser.arriveAndDeregister();
        };
        phaser.register();
        addFlux.subscribe(nextConsumer, errorConsumer, completeConsumer);
        phaser.arriveAndAwaitAdvance();
    }

}
