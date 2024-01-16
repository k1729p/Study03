package kp.company.handlers;

import static kp.Constants.DEPARTMENT_KEY_VAR;
import static kp.Constants.EMPLOYEE_FIRST_NAME_VAR;
import static kp.Constants.EMPLOYEE_LAST_NAME_VAR;
import static kp.Constants.NOT_FOUND_SUPPLIER;

import java.lang.invoke.MethodHandles;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import kp.company.domain.Department;
import kp.company.domain.Employee;
import reactor.core.publisher.Mono;

/**
 * The WebFlux handler for the {@link Employee}.<br/>
 * Uses reactive Redis operations for 'string' values.
 *
 */
@Component
public class EmployeeHandler {
	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	private static final boolean VERBOSE = false;

	private final ReactiveRedisOperations<String, Department> reactiveRedisOperations;

	/**
	 * The constructor.
	 * 
	 * @param reactiveRedisOperations the {@link ReactiveRedisOperations} for the
	 *                                {@link Department}s
	 */
	public EmployeeHandler(ReactiveRedisOperations<String, Department> reactiveRedisOperations) {
		this.reactiveRedisOperations = reactiveRedisOperations;
	}

	/**
	 * Finds the {@link Employee} by the {@link Department}'s key and the
	 * {@link Employee}'s names (the first name and the last name).
	 * 
	 * @param request the {@link ServerRequest}
	 * @return the {@link ServerResponse} {@link Mono} with the {@link Employee}
	 */
	public Mono<ServerResponse> handleEmployeeByDepartmentKeyAndNames(ServerRequest request) {

		final Mono<Department> departmentMono = Mono.just(DEPARTMENT_KEY_VAR).map(request::pathVariable)
				.flatMap(reactiveRedisOperations.opsForValue()::get);

		final Predicate<Employee> firstNamePredicate = emp -> request.queryParam(EMPLOYEE_FIRST_NAME_VAR)
				.map(param -> emp.firstName().equalsIgnoreCase(param)).orElse(true);
		final Predicate<Employee> lastNamePredicate = emp -> request.queryParam(EMPLOYEE_LAST_NAME_VAR)
				.map(param -> emp.lastName().equalsIgnoreCase(param)).orElse(true);

		final Function<Employee, Mono<ServerResponse>> okMapper = employee -> ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON).body(Mono.just(employee), Employee.class);
		final Function<Department, Mono<ServerResponse>> responseMapper = department -> department.employees().stream()
				.filter(firstNamePredicate.and(lastNamePredicate)).findFirst().map(okMapper)
				.orElseGet(NOT_FOUND_SUPPLIER);

		final Mono<ServerResponse> serverResponseMono = departmentMono.flatMap(responseMapper)
				.switchIfEmpty(NOT_FOUND_SUPPLIER.get())//
				.transform(mono -> VERBOSE ? mono.log() : mono);
		logger.info("handleEmployeeByDepartmentKeyAndNames():");
		return serverResponseMono;
	}

}
