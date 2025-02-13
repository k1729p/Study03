package kp.company.handlers.base;

import kp.company.domain.Department;
import org.mockito.Mockito;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static kp.Constants.VALUE_SCAN_OPTIONS;
import static kp.TestConstants.*;

/**
 * The base class for the handler tests using the mocked:
 * <ul>
 * <li>{@link ReactiveRedisOperations}</li>
 * <li>{@link ReactiveValueOperations}</li>
 * </ul>
 * <p>
 * The handler tests are designed to be run as integration tests (not as unit tests),
 * hence the use of {@link MockitoBean}.
 * </p>
 */
public abstract class ValueOperationsTestsBase extends HandlersTestsBase {

    /**
     * The {@link ReactiveRedisOperations}.
     */
    @MockitoBean
    protected ReactiveRedisOperations<String, Department> reactiveRedisOperations;

    /**
     * The {@link ReactiveValueOperations}.
     */
    @MockitoBean
    protected ReactiveValueOperations<String, Department> reactiveValueOperations;

    /**
     * Initializes the test.
     */
    protected void initialize() {

        Mockito.when(reactiveRedisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        Mockito.when(reactiveRedisOperations.scan(VALUE_SCAN_OPTIONS))
                .thenReturn(Flux.just(TEST_DEP_KEY_1, TEST_DEP_KEY_2));
        Mockito.when(reactiveValueOperations.get(TEST_DEP_KEY_1)).thenReturn(Mono.just(EXPECTED_DEPARTMENT_1));
        Mockito.when(reactiveValueOperations.get(TEST_DEP_KEY_2)).thenReturn(Mono.just(EXPECTED_DEPARTMENT_2));
    }

}
