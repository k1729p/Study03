package kp.company.handlers.base;

import static kp.Constants.VALUE_SCAN_OPTIONS;
import static kp.TestConstants.EXPECTED_DEPARTMENT_1;
import static kp.TestConstants.EXPECTED_DEPARTMENT_2;
import static kp.TestConstants.TEST_DEP_KEY_1;
import static kp.TestConstants.TEST_DEP_KEY_2;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import kp.company.domain.Department;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The base class for the handler tests using the mocked
 * {@link ReactiveRedisOperations} and {@link ReactiveValueOperations}.
 * 
 */
public abstract class ValueOperationsTestBase {

	/**
	 * The {@link RouterFunction}.
	 */
	@Autowired
	protected RouterFunction<ServerResponse> routerFunction;

	/**
	 * The {@link WebTestClient}.
	 */
	protected WebTestClient webTestClient;

	/**
	 * The {@link ReactiveRedisOperations}.
	 */
	@MockBean
	protected ReactiveRedisOperations<String, Department> reactiveRedisOperations;

	/**
	 * The {@link ReactiveValueOperations}.
	 */
	@MockBean
	protected ReactiveValueOperations<String, Department> reactiveValueOperations;

	/**
	 * The constructor.
	 */
	public ValueOperationsTestBase() {
		super();
	}

	/**
	 * Initializes the test.
	 * 
	 */
	protected void initialize() {

		Mockito.when(reactiveRedisOperations.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveRedisOperations.scan(VALUE_SCAN_OPTIONS))
				.thenReturn(Flux.just(TEST_DEP_KEY_1, TEST_DEP_KEY_2));
		Mockito.when(reactiveValueOperations.get(TEST_DEP_KEY_1)).thenReturn(Mono.just(EXPECTED_DEPARTMENT_1));
		Mockito.when(reactiveValueOperations.get(TEST_DEP_KEY_2)).thenReturn(Mono.just(EXPECTED_DEPARTMENT_2));
		webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
	}
}
