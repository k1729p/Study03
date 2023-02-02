package kp.client;

import static kp.Constants.DEP_KEY_FUN;
import static kp.Constants.DEP_KEY_INDEX_LOWER_BOUND;
import static kp.Constants.EMP_INDEX_FUN;
import static kp.Constants.EMP_INDEX_LOWER_BOUND;
import static kp.Constants.LOAD_SAMPLE_DATASET_PATH;

import java.lang.invoke.MethodHandles;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import kp.client.subscribers.DepartmentSubscriber;
import kp.client.subscribers.EmployeeSubscriber;
import kp.client.subscribers.TeamSubscriber;
import kp.company.domain.Employee;
import reactor.core.publisher.Mono;

/**
 * The {@link WebClient} launcher.<br/>
 * 
 * The {@link WebClient} is the non-blocking, reactive client.
 */
public class WebClientLauncher {
	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	private static final boolean VERBOSE = false;

	private static final String WEB_CLIENT_BASE_URL = "http://localhost:8280";
	private static final String SUBSCRIBED_DEPARTMENT_KEY = DEP_KEY_FUN.apply(DEP_KEY_INDEX_LOWER_BOUND);
	private static final String SUBSCRIBED_EMPLOYEE_FIRST_NAME = Employee
			.fromIndex(EMP_INDEX_FUN.applyAsInt(DEP_KEY_INDEX_LOWER_BOUND, EMP_INDEX_LOWER_BOUND)).firstName();
	private static final String SUBSCRIBED_EMPLOYEE_LAST_NAME = Employee
			.fromIndex(EMP_INDEX_FUN.applyAsInt(DEP_KEY_INDEX_LOWER_BOUND, EMP_INDEX_LOWER_BOUND)).lastName();
	private static final String SUBSCRIBED_TEAM_RANGE_FROM = "1";
	private static final String SUBSCRIBED_TEAM_RANGE_TO = "3";
	private static final String SUBSCRIBED_TEAM_ID_1 = "1";
	private static final String SUBSCRIBED_TEAM_ID_2 = "3";
	private static final String SUBSCRIBED_TEAM_ID_3 = "5";

	/**
	 * The constructor.
	 */
	public WebClientLauncher() {
		super();
	}

	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		try {
			new WebClientLauncher().performRequests();
		} catch (WebClientResponseException e) {
			logger.error("main(): WebClientResponseException[" + e.getMessage() + "]");
		} catch (Exception e) {
			logger.error("main(): Exception[" + e.getMessage() + "]");
		}
	}

	/**
	 * Performs HTTP requests with the {@link WebClient}.
	 * 
	 */
	private void performRequests() {

		final WebClient client = WebClient.create(WEB_CLIENT_BASE_URL);
		subscribeSampleDatasetLoading(client);

		final DepartmentSubscriber departmentSubscriber = new DepartmentSubscriber(client);
		departmentSubscriber.subscribeDepartments();
		departmentSubscriber.subscribeDepartment(SUBSCRIBED_DEPARTMENT_KEY);

		final EmployeeSubscriber employeeSubscriber = new EmployeeSubscriber(client);
		employeeSubscriber.subscribeEmployee(SUBSCRIBED_DEPARTMENT_KEY, SUBSCRIBED_EMPLOYEE_FIRST_NAME,
				SUBSCRIBED_EMPLOYEE_LAST_NAME);

		final TeamSubscriber teamSubscriber = new TeamSubscriber(client);
		teamSubscriber.subscribeTeams();
		teamSubscriber.subscribeTeamsRangeByScore(SUBSCRIBED_TEAM_RANGE_FROM, SUBSCRIBED_TEAM_RANGE_TO);
		teamSubscriber.subscribeZippedTeamRankById(SUBSCRIBED_TEAM_ID_1, SUBSCRIBED_TEAM_ID_2, SUBSCRIBED_TEAM_ID_3);
	}

	/**
	 * Loads sample dataset.
	 * 
	 * @param client the {@link WebClient}
	 */
	private void subscribeSampleDatasetLoading(WebClient client) {

		final Mono<String> sampleDatasetLoadingMono = client.get().uri(LOAD_SAMPLE_DATASET_PATH).retrieve()
				.bodyToMono(String.class)/*-*/
				.transform(mono -> VERBOSE ? mono.log() : mono);

		sampleDatasetLoadingMono.blockOptional().ifPresentOrElse(/*-*/
				result -> logger.info("subscribeSampleDatasetLoading(): " + result),
				() -> logger.info("subscribeSampleDatasetLoading(): mono completed empty"));
	}

}
