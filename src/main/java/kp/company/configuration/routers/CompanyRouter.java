package kp.company.configuration.routers;

import kp.company.handlers.DepartmentHandler;
import kp.company.handlers.EmployeeHandler;
import kp.company.handlers.SampleDatasetHandler;
import kp.company.handlers.TeamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.lang.invoke.MethodHandles;

import static kp.Constants.*;

/**
 * The router for the handlers.
 * <p>
 * The 'WebFlux.fn':
 * </p>
 * <ul>
 * <li>is a lightweight functional programming model</li>
 * <li>is an alternative to the annotation-based programming model</li>
 * <li>separates the routing configuration from the actual handling of the requests</li>
 * </ul>
 * <p>
 * The difference between models:
 * </p>
 * <ul>
 * <li>the Reactive model is a push model</li>
 * <li>the Java 8 Streams are a pull model</li>
 * </ul>
 * <p>
 * The reactive publishers:
 * </p>
 * <ul>
 * <li>the Mono represents a single asynchronous value</li>
 * <li>the Flux represents a stream of asynchronous values</li>
 * </ul>
 * <p>
 * The difference between publishers:
 * </p>
 * <ul>
 * <li>the Mono type represents a single valued or empty Flux</li>
 * <li>the Flux type is a publisher of a sequence of events</li>
 * </ul>
 */
@Configuration
public class CompanyRouter {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Creates the router function.
     *
     * @param sampleDatasetHandler the {@link SampleDatasetHandler}
     * @param departmentHandler    the {@link DepartmentHandler}
     * @param employeeHandler      the {@link EmployeeHandler}
     * @param teamHandler          the {@link TeamHandler}
     * @return the router function
     */
    @Bean
    public RouterFunction<ServerResponse> createRouterFunction(SampleDatasetHandler sampleDatasetHandler,
                                                               DepartmentHandler departmentHandler,
                                                               EmployeeHandler employeeHandler,
                                                               TeamHandler teamHandler) {

        final RouterFunction<ServerResponse> routerFunction = RouterFunctions.route()
                .GET(LOAD_SAMPLE_DATASET_PATH, sampleDatasetHandler::handleSampleDatasetLoading)
                .GET(GET_EMPLOYEE_PATH, employeeHandler::handleEmployeeByDepartmentKeyAndNames)
                .GET(GET_DEPARTMENT_PATH, departmentHandler::handleDepartmentByDepartmentKey)
                .GET(GET_DEPARTMENTS_PATH, departmentHandler::handleDepartments)
                .GET(GET_TEAMS_PATH, teamHandler::handleTeams)
                .GET(GET_TEAMS_RANGE_PATH, teamHandler::handleTeamsRangeByScore)
                .GET(GET_TEAM_RANK_PATH, teamHandler::handleTeamRankById)
                .build();
        logger.debug("route(): Router function created");
        return routerFunction;
    }
}