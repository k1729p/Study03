package kp.company.handlers;

import static kp.Constants.DEPARTMENT_KEY_VAR;
import static kp.Constants.NOT_FOUND_SUPPLIER;
import static kp.Constants.VALUE_SCAN_OPTIONS;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import kp.company.domain.Department;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The WebFlux handler for the {@link Department}.<br/>
 * Uses reactive Redis operations for 'string' values.
 *
 */
@Component
public class DepartmentHandler {
	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	private static final boolean VERBOSE = false;

	private final ReactiveRedisOperations<String, Department> reactiveRedisOperations;

	/**
	 * The constructor.
	 * 
	 * @param reactiveRedisOperations the {@link ReactiveRedisOperations} for the
	 *                                {@link Department}s
	 */
	public DepartmentHandler(ReactiveRedisOperations<String, Department> reactiveRedisOperations) {
		this.reactiveRedisOperations = reactiveRedisOperations;
	}

	/**
	 * Finds the list of {@link Department}s.
	 * 
	 * @param request the {@link ServerRequest}
	 * @return the {@link ServerResponse} {@link Mono} with the {@link Department}s
	 */
	public Mono<ServerResponse> handleDepartments(ServerRequest request) {

		final Flux<Department> departmentFlux = reactiveRedisOperations.scan(VALUE_SCAN_OPTIONS)
				.flatMap(reactiveRedisOperations.opsForValue()::get);

		final Function<List<Department>, Mono<ServerResponse>> responseMapper = list -> list.isEmpty()
				? NOT_FOUND_SUPPLIER.get()
				: ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(list), List.class);

		final Mono<ServerResponse> serverResponseMono = departmentFlux.collectList().flatMap(responseMapper)/*-*/
				.transform(mono -> VERBOSE ? mono.log() : mono);
		logger.info("handleDepartments():");
		return serverResponseMono;
	}

	/**
	 * Finds the {@link Department} by {@link Department} key.
	 * 
	 * @param request the {@link ServerRequest}
	 * @return the {@link ServerResponse} {@link Mono} with the {@link Department}
	 */
	public Mono<ServerResponse> handleDepartmentByDepartmentKey(ServerRequest request) {

		final Mono<Department> departmentMono = Mono.just(DEPARTMENT_KEY_VAR).map(request::pathVariable)
				.flatMap(reactiveRedisOperations.opsForValue()::get);

		final Function<Department, Mono<ServerResponse>> responseMapper = department -> ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON).body(Mono.just(department), Department.class);

		final Mono<ServerResponse> serverResponseMono = departmentMono.flatMap(responseMapper)
				.switchIfEmpty(NOT_FOUND_SUPPLIER.get())/*-*/
				.transform(mono -> VERBOSE ? mono.log() : mono);
		logger.info("handleDepartmentByDepartmentKey():");
		return serverResponseMono;
	}

}