package kp.company.configuration.routers;

import static kp.Constants.GET_DEPARTMENTS_PATH;
import static kp.Constants.GET_DEPARTMENT_PATH;
import static kp.Constants.GET_EMPLOYEE_PATH;
import static kp.Constants.GET_TEAMS_PATH;
import static kp.Constants.GET_TEAMS_RANGE_PATH;
import static kp.Constants.GET_TEAM_RANK_PATH;
import static kp.Constants.LOAD_SAMPLE_DATASET_PATH;

import java.lang.invoke.MethodHandles;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import kp.company.handlers.DepartmentHandler;
import kp.company.handlers.EmployeeHandler;
import kp.company.handlers.SampleDatasetHandler;
import kp.company.handlers.TeamHandler;

/*-
The 'WebFlux.fn':
 - is a lightweight functional programming model
 - is an alternative to the annotation-based programming model
 - separates the routing configuration from the actual handling of the requests

The difference between models:
 - the Reactive is a push model
 - the Java 8 Streams are a pull model
 
The reactive publishers:
 - the Mono represents a single asynchronous value
 - the Flux represents a stream of asynchronous values
 
The difference between publishers:
 - the Mono type represents a single valued or empty Flux
 - the Flux type is a publisher of a sequence of events
 */
/**
 * The router for the handlers.
 *
 */
@Configuration
public class CompanyRouter {
	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	/**
	 * The constructor.
	 */
	public CompanyRouter() {
		super();
	}

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
			DepartmentHandler departmentHandler, EmployeeHandler employeeHandler, TeamHandler teamHandler) {

		final RouterFunction<ServerResponse> routerFunction = RouterFunctions.route()
				.GET(LOAD_SAMPLE_DATASET_PATH, sampleDatasetHandler::handleSampleDatasetLoading)
				.GET(GET_EMPLOYEE_PATH, employeeHandler::handleEmployeeByDepartmentKeyAndNames)
				.GET(GET_DEPARTMENT_PATH, departmentHandler::handleDepartmentByDepartmentKey)
				.GET(GET_DEPARTMENTS_PATH, departmentHandler::handleDepartments)
				.GET(GET_TEAMS_PATH, teamHandler::handleTeams)
				.GET(GET_TEAMS_RANGE_PATH, teamHandler::handleTeamsRangeByScore)
				.GET(GET_TEAM_RANK_PATH, teamHandler::handleTeamRankById).build();
		logger.debug("route():");
		return routerFunction;
	}
}