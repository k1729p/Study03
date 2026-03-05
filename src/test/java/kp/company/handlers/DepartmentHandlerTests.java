package kp.company.handlers;

import kp.company.domain.Department;
import kp.company.domain.Employee;
import kp.company.handlers.base.ValueOperationsTestsBase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.function.Function;

import static kp.Constants.*;
import static kp.TestConstants.*;

/**
 * The {@link DepartmentHandler} tests.
 * <p>
 * The tests use {@link WebTestClient}.
 * </p>
 */
class DepartmentHandlerTests extends ValueOperationsTestsBase {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Should get the list of {@link Department}s.
     */
    @Test
    void shouldGetDepartments() {
        // GIVEN
        initialize();
        // WHEN
        final ResponseSpec responseSpec = webTestClient.get().uri(GET_DEPARTMENTS_PATH)
                .accept(MediaType.APPLICATION_JSON).exchange();
        // THEN
        responseSpec.expectStatus().isOk();
        responseSpec.expectBodyList(Department.class).value(this::checkDepartments);
        logger.info("shouldGetDepartments():");
    }

    /**
     * Should get the {@link Department} by the {@link Department} key.
     */
    @Test
    void shouldGetDepartmentByDepartmentKey() {
        // GIVEN
        initialize();
        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_DEPARTMENT_PATH)
                .build(TEST_DEP_KEY_1);
        // WHEN
        final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isOk();
        responseSpec.expectBody(Department.class).value(this::checkDepartment);
        logger.info("shouldGetDepartmentByDepartmentKey():");
    }

    /**
     * Should not get the list of {@link Department}s and get status 'Not Found'.
     */
    @Test
    void shouldNotGetDepartmentsAndGetStatusNotFound() {
        // GIVEN
        Mockito.when(reactiveRedisOperations.opsForValue()).thenReturn(reactiveValueOperations);
        Mockito.when(reactiveRedisOperations.scan(VALUE_SCAN_OPTIONS)).thenReturn(Flux.empty());
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
        // WHEN
        final ResponseSpec responseSpec = webTestClient.get().uri(GET_DEPARTMENTS_PATH)
                .accept(MediaType.APPLICATION_JSON).exchange();
        // THEN
        responseSpec.expectStatus().isNotFound();
        logger.info("shouldNotGetDepartmentsAndGetStatusNotFound():");
    }

    /**
     * Should not get the {@link Department} by the unknown key and get status 'Not Found'.
     */
    @Test
    void shouldNotGetDepartmentByUnknownKeyAndGetStatusNotFound() {
        // GIVEN
        initialize();
        Mockito.when(reactiveValueOperations.get(TEST_DEPARTMENT_KEY_UNKNOWN)).thenReturn(Mono.empty());
        final Function<UriBuilder, URI> uriFunction = uriBuilder -> uriBuilder.path(GET_DEPARTMENT_PATH)
                .build(TEST_DEPARTMENT_KEY_UNKNOWN);
        // WHEN
        final ResponseSpec responseSpec = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON)
                .exchange();
        // THEN
        responseSpec.expectStatus().isNotFound();
        logger.info("shouldNotGetDepartmentByUnknownKeyAndGetStatusNotFound():");
    }

    /**
     * Checks the {@link Department} list.
     *
     * @param departmentList the list of {@link Department}s
     */
    private void checkDepartments(List<Department> departmentList) {

        Assertions.assertNotNull(departmentList, DEP_LIST_NULL_ERR_MSG);
        Assertions.assertEquals(TEST_DATASET_MAP.size(), departmentList.size(), DEP_LIST_SIZE_ERR_MSG);
        final Department[] expected = TEST_DATASET_MAP.values().toArray(new Department[0]);
        MatcherAssert.assertThat(DEP_LIST_CONTENT_ERR_MSG, departmentList, Matchers.containsInAnyOrder(expected));
    }

    /**
     * Checks the {@link Department}.
     *
     * @param department the {@link Department}
     */
    private void checkDepartment(Department department) {

        Assertions.assertNotNull(department, DEP_NULL_ERR_MSG);
        Assertions.assertEquals(EXPECTED_DEPARTMENT_1.name(), department.name(), DEP_NAME_ERR_MSG);
        checkEmployees(department.employees());
    }

    /**
     * Checks the employee list.
     *
     * @param employeeList the employee list
     */
    private void checkEmployees(List<Employee> employeeList) {

        Assertions.assertNotNull(employeeList, EMP_LIST_NULL_ERR_MSG);
        Assertions.assertEquals(EXPECTED_DEPARTMENT_1.employees().size(), employeeList.size(), EMP_LIST_SIZE_ERR_MSG);
        final Employee[] expected = EXPECTED_DEPARTMENT_1.employees().toArray(new Employee[0]);
        MatcherAssert.assertThat(EMP_LIST_CONTENT_ERR_MSG, employeeList, Matchers.containsInAnyOrder(expected));
    }

}
