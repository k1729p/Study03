package kp.company.handlers;

import static kp.Constants.EMPLOYEE_FIRST_NAME_VAR;
import static kp.Constants.EMPLOYEE_LAST_NAME_VAR;
import static kp.Constants.GET_EMPLOYEE_PATH;
import static kp.TestConstants.EMP_F_NAME_ERR_MSG;
import static kp.TestConstants.EMP_L_NAME_ERR_MSG;
import static kp.TestConstants.EMP_NULL_ERR_MSG;
import static kp.TestConstants.EXPECTED_EMPLOYEE_1;
import static kp.TestConstants.TEST_DEPARTMENT_KEY_UNKNOWN;
import static kp.TestConstants.TEST_DEP_KEY_1;
import static kp.TestConstants.TEST_EMPLOYEE_NAME_UNKNOWN;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;

import kp.company.domain.Department;
import kp.company.domain.Employee;
import kp.company.handlers.base.ValueOperationsTestBase;
import reactor.core.publisher.Mono;

/**
 * The {@link EmployeeHandler} tests.<br/>
 * The tests use {@link WebTestClient}.
 *
 */
@SpringBootTest
class EmployeeHandlerTests extends ValueOperationsTestBase {
	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	/**
	 * The constructor.
	 */
	EmployeeHandlerTests() {
		super();
	}

	/**
	 * Should get the {@link Employee} by the {@link Department} key and names.
	 * 
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
		logger.debug("shouldGetEmployeeByDepartmentKeyAndNames():");
	}

	/**
	 * Should not get the {@link Employee} by unknown {@link Department} key and
	 * correct names.
	 * 
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
		logger.debug("shouldNotGetEmployeeByUnknownKeyAndGetStatusNotFound():");
	}

	/**
	 * Should not get the {@link Employee} by correct {@link Department} key and
	 * unknown names.
	 * 
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
		logger.debug("shouldNotGetEmployeeByUnknownNamesAndGetStatusNotFound():");
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
