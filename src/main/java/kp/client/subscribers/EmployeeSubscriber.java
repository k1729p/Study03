package kp.client.subscribers;

import static kp.Constants.EMPLOYEE_FIRST_NAME_VAR;
import static kp.Constants.EMPLOYEE_LAST_NAME_VAR;
import static kp.Constants.GET_EMPLOYEE_PATH;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import kp.company.domain.Employee;
import reactor.core.publisher.Mono;

/**
 * The subscriber for the employees.
 *
 */
public class EmployeeSubscriber {
	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	private static final boolean VERBOSE = false;

	private final WebClient client;

	/**
	 * The constructor.
	 * 
	 * @param client the {@link WebClient}
	 */
	public EmployeeSubscriber(WebClient client) {
		this.client = client;
	}

	/**
	 * Finds an employee by the department key and employee names (the first name
	 * and the last name).
	 * 
	 * @param departmentKey department key
	 * @param firstName     first name
	 * @param lastName      last name
	 */
	public void subscribeEmployee(String departmentKey, String firstName, String lastName) {

		final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_EMPLOYEE_PATH)
				.queryParam(EMPLOYEE_FIRST_NAME_VAR, firstName).queryParam(EMPLOYEE_LAST_NAME_VAR, lastName)
				.build(departmentKey);
		final Mono<Employee> employeeMono = client.get().uri(uriFunction).retrieve().bodyToMono(Employee.class)/*-*/
				.transform(mono -> VERBOSE ? mono.log() : mono);

		employeeMono.blockOptional().ifPresentOrElse(/*-*/
				emp -> logger.info(String.format(
						"subscribeEmployee(): department key[%s], employee firstName[%s], employee lastName[%s]",
						departmentKey, emp.firstName(), emp.lastName())),
				() -> logger.info("subscribeEmployee(): mono completed empty"));
	}

}
