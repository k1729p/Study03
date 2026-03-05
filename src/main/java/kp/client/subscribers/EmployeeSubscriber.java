package kp.client.subscribers;

import kp.company.domain.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.function.Function;

import static kp.Constants.*;

/**
 * The subscriber for the {@link Employee}s.
 */
public class EmployeeSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final boolean VERBOSE = false;

    private final WebClient client;

    /**
     * Constructor.
     *
     * @param client the {@link WebClient}
     */
    public EmployeeSubscriber(WebClient client) {
        this.client = client;
    }

    /**
     * Subscribes to an {@link Employee} by the {@link kp.company.domain.Department} key and
     * {@link Employee} names (first name and last name).
     *
     * @param departmentKey {@link kp.company.domain.Department} key
     * @param firstName     first name
     * @param lastName      last name
     */
    public void subscribeEmployee(String departmentKey, String firstName, String lastName) {

        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_EMPLOYEE_PATH)
                .queryParam(EMPLOYEE_FIRST_NAME_VAR, firstName)
                .queryParam(EMPLOYEE_LAST_NAME_VAR, lastName)
                .build(departmentKey);
        final Mono<Employee> employeeMono = client.get().uri(uriFunction).retrieve().bodyToMono(Employee.class)
                .transform(mono -> VERBOSE ? mono.log() : mono);

        employeeMono.blockOptional().ifPresentOrElse(
                emp -> logger.info(
                        "subscribeEmployee(): department key[{}], employee firstName[{}], employee lastName[{}]",
                        departmentKey, emp.firstName(), emp.lastName()),
                () -> logger.info("subscribeEmployee(): mono completed empty"));
    }

}
