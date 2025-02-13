package kp.company.handlers;

import kp.company.domain.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Function;

import static kp.Constants.*;

/**
 * The WebFlux handler for the {@link Department}.
 * <p>
 * Uses reactive Redis operations for 'string' values.
 * </p>
 */
@Component
public class DepartmentHandler {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final boolean VERBOSE = false;

    private final ReactiveRedisOperations<String, Department> reactiveRedisOperations;

    /**
     * Constructor.
     *
     * @param reactiveRedisOperations the {@link ReactiveRedisOperations} for the {@link Department}.
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

        logger.debug("handleDepartments(): request without query parameters[{}]", request.queryParams().isEmpty());
        final Flux<Department> departmentFlux = reactiveRedisOperations.scan(VALUE_SCAN_OPTIONS)
                .flatMap(reactiveRedisOperations.opsForValue()::get);

        final Function<List<Department>, Mono<ServerResponse>> responseMapper = list -> list.isEmpty()
                ? NOT_FOUND_SUPPLIER.get()
                : ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list);

        final Mono<ServerResponse> serverResponseMono = departmentFlux.collectList().flatMap(responseMapper)
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
                .contentType(MediaType.APPLICATION_JSON).bodyValue(department);

        final Mono<ServerResponse> serverResponseMono = departmentMono.flatMap(responseMapper)
                .switchIfEmpty(NOT_FOUND_SUPPLIER.get())
                .transform(mono -> VERBOSE ? mono.log() : mono);
        logger.info("handleDepartmentByDepartmentKey():");
        return serverResponseMono;
    }

}