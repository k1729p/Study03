package kp;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.DoubleFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * The constants.
 */
@SuppressWarnings("doclint:missing")
public final class Constants {
    private static final String ROOT = "/";
    public static final String LOAD_SAMPLE_DATASET_PATH = ROOT + "loadSampleDataset";
    public static final String GET_DEPARTMENTS_PATH = ROOT + "company/departments";
    public static final String GET_DEPARTMENT_PATH = GET_DEPARTMENTS_PATH + "/{departmentKey}";
    public static final String GET_EMPLOYEE_PATH = GET_DEPARTMENT_PATH + "/employees";
    public static final String GET_TEAMS_PATH = ROOT + "company/teams";
    public static final String GET_TEAMS_RANGE_PATH = GET_TEAMS_PATH + "/range";
    public static final String GET_TEAM_RANK_PATH = GET_TEAMS_PATH + "/rank";

    public static final String DEPARTMENT_KEY_VAR = "departmentKey";
    public static final String EMPLOYEE_FIRST_NAME_VAR = "firstName";
    public static final String EMPLOYEE_LAST_NAME_VAR = "lastName";
    public static final String TEAM_ID_VAR = "id";
    public static final String RANGE_FROM_VAR = "rangeFrom";
    public static final String RANGE_TO_VAR = "rangeTo";

    public static final String LOAD_SAMPLE_DATASET_RESULT_JSON = "{\"result\":\"The sample dataset was loaded with success.\"}";
    public static final IntFunction<String> DEP_KEY_FUN = "K-DEP-%d"::formatted;
    public static final IntFunction<String> DEP_NAME_FUN = "D-Name-%d"::formatted;
    public static final IntBinaryOperator EMP_INDEX_FUN = (depIndex, empIndex) -> 100 * depIndex + empIndex;
    public static final int DEP_KEY_INDEX_LOWER_BOUND = 1;
    public static final int DEP_KEY_INDEX_UPPER_BOUND = 2;
    public static final int EMP_INDEX_LOWER_BOUND = 1;
    public static final int EMP_INDEX_UPPER_BOUND = 2;
    public static final Supplier<Mono<ServerResponse>> NOT_FOUND_SUPPLIER = () -> ServerResponse.notFound().build();
    public static final ScanOptions VALUE_SCAN_OPTIONS = ScanOptions.scanOptions().type(DataType.STRING).build();

    public static final String TEAMS_KEY = "K-TEAMS";
    public static final int TEAM_ID_LOWER_BOUND = 1;
    public static final int TEAMS_COUNT = 5;
    public static final DoubleFunction<String> DBL_FMT = "%.0f"::formatted;

    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}
