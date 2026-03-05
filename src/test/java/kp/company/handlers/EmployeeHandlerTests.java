package kp.company.handlers;

import kp.company.domain.Department;
import kp.company.domain.Employee;
import kp.company.handlers.base.ValueOperationsTestsBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.function.Function;

import static kp.Constants.*;
import static kp.TestConstants.*;

/**
 * The {@link EmployeeHandler} tests.
 * <p>
 * The tests use {@link WebTestClient}.
 * </p>
 */
class EmployeeHandlerTests extends ValueOperationsTestsBase {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Should get the {@link Employee} by the {@link Department} key and names.
     */
    @Test
    void shouldGetEmployeeByDepartmentKeyAndNames() {
        // GIVEN
        initialize();
        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_EMPLOYEE_PATH)
                .queryParam(EMPLOYEE_FIRST_NAME_VAR, EXPECTED_EMPLOYEE_1.firstName())
                .queryParam(EMPLOYEE_LAST_NAME_VAR, EXPECTED_EMPLOYEE_1.lastName()).build(TEST_DEP_KEY_1);
        // WHEN
        final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isOk();
        responseSpec.expectBody(Employee.class).value(this::checkEmployee);
        logger.info("shouldGetEmployeeByDepartmentKeyAndNames():");
    }

    /**
     * Should not get the {@link Employee} by unknown {@link Department} key and correct employee names.
     */
    @Test
    void shouldNotGetEmployeeByUnknownKeyAndGetStatusNotFound() {
        // GIVEN
        initialize();
        Mockito.when(reactiveValueOperations.get(TEST_DEPARTMENT_KEY_UNKNOWN)).thenReturn(Mono.empty());
        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_EMPLOYEE_PATH)
                .queryParam(EMPLOYEE_FIRST_NAME_VAR, EXPECTED_EMPLOYEE_1.firstName())
                .queryParam(EMPLOYEE_LAST_NAME_VAR, EXPECTED_EMPLOYEE_1.lastName()).build(TEST_DEPARTMENT_KEY_UNKNOWN);
        // WHEN
        final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isNotFound();
        logger.info("shouldNotGetEmployeeByUnknownKeyAndGetStatusNotFound():");
    }

    /**
     * Should not get the {@link Employee} by correct {@link Department} key and unknown employee names.
     */
    @Test
    void shouldNotGetEmployeeByUnknownNamesAndGetStatusNotFound() {
        // GIVEN
        initialize();
        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_EMPLOYEE_PATH)
                .queryParam(EMPLOYEE_FIRST_NAME_VAR, TEST_EMPLOYEE_NAME_UNKNOWN)
                .queryParam(EMPLOYEE_LAST_NAME_VAR, TEST_EMPLOYEE_NAME_UNKNOWN).build(TEST_DEP_KEY_1);
        // WHEN
        final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isNotFound();
        logger.info("shouldNotGetEmployeeByUnknownNamesAndGetStatusNotFound():");
    }

    /**
     * Checks the {@link Employee}.
     *
     * @param employee the {@link Employee}
     */
    private void checkEmployee(Employee employee) {

        Assertions.assertNotNull(employee, EMP_NULL_ERR_MSG);
        Assertions.assertEquals(EXPECTED_EMPLOYEE_1.firstName(), employee.firstName(), EMP_F_NAME_ERR_MSG);
        Assertions.assertEquals(EXPECTED_EMPLOYEE_1.lastName(), employee.lastName(), EMP_L_NAME_ERR_MSG);
    }

}
