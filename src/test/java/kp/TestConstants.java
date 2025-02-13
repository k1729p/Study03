package kp;

import kp.company.domain.Department;
import kp.company.domain.Employee;

import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static kp.Constants.*;

/**
 * Test constants.
 */
@SuppressWarnings("doclint:missing")
public final class TestConstants {
    public static final int TEST_DEP_KEY_INDEX_1 = 1;
    public static final int TEST_DEP_KEY_INDEX_2 = 2;
    public static final String TEST_DEP_KEY_1 = DEP_KEY_FUN.apply(TEST_DEP_KEY_INDEX_1);
    public static final String TEST_DEP_KEY_2 = DEP_KEY_FUN.apply(TEST_DEP_KEY_INDEX_2);
    public static final int TEST_EMP_INDEX_1 = 1;
    public static final int TEST_EMP_INDEX_2 = 2;

    public static final IntFunction<List<Employee>> TEST_EMP_LIST_FUN = depIndex -> IntStream
            .rangeClosed(TEST_EMP_INDEX_1, TEST_EMP_INDEX_2).boxed()
            .map(empIndex -> EMP_INDEX_FUN.applyAsInt(depIndex, empIndex)).map(Employee::fromIndex).toList();

    public static final IntFunction<Map.Entry<String, Department>> TEST_MAP_ENTRY_FUN = depIndex -> Map.entry(
            DEP_KEY_FUN.apply(depIndex),
            new Department(DEP_NAME_FUN.apply(depIndex), TEST_EMP_LIST_FUN.apply(depIndex)));

    public static final Map<String, Department> TEST_DATASET_MAP = IntStream
            .rangeClosed(TEST_DEP_KEY_INDEX_1, TEST_DEP_KEY_INDEX_2).boxed().map(TEST_MAP_ENTRY_FUN::apply)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static final Department EXPECTED_DEPARTMENT_1 = TEST_DATASET_MAP.get(TEST_DEP_KEY_1);
    public static final Department EXPECTED_DEPARTMENT_2 = TEST_DATASET_MAP.get(TEST_DEP_KEY_2);
    public static final Employee EXPECTED_EMPLOYEE_1 = Employee
            .fromIndex(EMP_INDEX_FUN.applyAsInt(TEST_DEP_KEY_INDEX_1, TEST_EMP_INDEX_1));

    public static final String TEST_DEPARTMENT_KEY_UNKNOWN = "unknown";
    public static final String TEST_EMPLOYEE_NAME_UNKNOWN = "unknown";

    public static final String DEP_NULL_ERR_MSG = "Department is null";
    public static final String DEP_NAME_ERR_MSG = "Bad department name";
    public static final String DEP_LIST_NULL_ERR_MSG = "List of departments is null";
    public static final String DEP_LIST_SIZE_ERR_MSG = "Bad department list size";
    public static final String DEP_LIST_CONTENT_ERR_MSG = "Bad department list content";
    public static final String EMP_NULL_ERR_MSG = "Employee is null";
    public static final String EMP_F_NAME_ERR_MSG = "Bad employee first name";
    public static final String EMP_L_NAME_ERR_MSG = "Bad employee last name";
    public static final String EMP_LIST_NULL_ERR_MSG = "List of employees is null";
    public static final String EMP_LIST_SIZE_ERR_MSG = "Bad employee list size";
    public static final String EMP_LIST_CONTENT_ERR_MSG = "Bad employee list content";

    private TestConstants() {
        throw new IllegalStateException("Utility class");
    }
}
