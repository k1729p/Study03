package kp.company.handlers;

import kp.company.domain.Department;
import kp.company.domain.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.util.function.Function;
import java.util.function.Predicate;

import static kp.Constants.*;

/**
 * The WebFlux handler for the {@link Employee}.
 * <p>
 * Uses reactive Redis operations for 'string' values.
 * </p>
 */
@Component
public class EmployeeHandler {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final boolean VERBOSE = false;

    private final ReactiveRedisOperations<String, Department> reactiveRedisOperations;

    /**
     * Constructor.
     *
     * @param reactiveRedisOperations the {@link ReactiveRedisOperations} for the {@link Department}s
     */
    public EmployeeHandler(ReactiveRedisOperations<String, Department> reactiveRedisOperations) {
        this.reactiveRedisOperations = reactiveRedisOperations;
    }

    /**
     * Finds the {@link Employee} by the {@link Department}'s key and the {@link Employee}'s names
     * (the first name and the last name).
     *
     * @param request the {@link ServerRequest}
     * @return the {@link ServerResponse} {@link Mono} with the {@link Employee}
     */
    public Mono<ServerResponse> handleEmployeeByDepartmentKeyAndNames(ServerRequest request) {

        final Mono<Department> departmentMono = Mono.just(DEPARTMENT_KEY_VAR).map(request::pathVariable)
                .flatMap(reactiveRedisOperations.opsForValue()::get);

        final Mono<ServerResponse> serverResponseMono = departmentMono.flatMap(getResponseMapper(request))
                .switchIfEmpty(NOT_FOUND_SUPPLIER.get())
                .transform(mono -> VERBOSE ? mono.log() : mono);
        logger.info("handleEmployeeByDepartmentKeyAndNames():");
        return serverResponseMono;
    }

    /**
     * Gets the response mapper.
     *
     * @param request the {@link ServerRequest}
     * @return the response mapper
     */
    private static Function<Department, Mono<ServerResponse>> getResponseMapper(ServerRequest request) {

        final Predicate<Employee> lastNamePredicate = emp -> request.queryParam(EMPLOYEE_LAST_NAME_VAR)
                .map(param -> emp.lastName().equalsIgnoreCase(param)).orElse(true);
        final Predicate<Employee> firstNamePredicate = emp -> request.queryParam(EMPLOYEE_FIRST_NAME_VAR)
                .map(param -> emp.firstName().equalsIgnoreCase(param)).orElse(true);

        final Function<Employee, Mono<ServerResponse>> okMapper = employee -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON).bodyValue(employee);
        return department -> department.employees().stream()
                .filter(firstNamePredicate.and(lastNamePredicate)).findFirst().map(okMapper)
                .orElseGet(NOT_FOUND_SUPPLIER);
    }

}
