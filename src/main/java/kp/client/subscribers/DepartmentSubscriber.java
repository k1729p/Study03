package kp.client.subscribers;

import static kp.Constants.GET_DEPARTMENTS_PATH;
import static kp.Constants.GET_DEPARTMENT_PATH;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Phaser;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.reactive.function.client.WebClient;

import kp.company.domain.Department;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The subscriber for the departments.
 *
 */
public class DepartmentSubscriber {
	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	private static final boolean VERBOSE = false;

	private final WebClient client;

	/**
	 * The constructor.
	 * 
	 * @param client the {@link WebClient}
	 */
	public DepartmentSubscriber(WebClient client) {
		this.client = client;
	}

	/**
	 * Finds all departments.
	 * 
	 */
	public void subscribeDepartments() {

		final Flux<Department> departmentsFlux = client.get().uri(GET_DEPARTMENTS_PATH).retrieve()
				.bodyToFlux(Department.class)/*-*/
				.transform(flux -> VERBOSE ? flux.log() : flux);

		final Phaser phaser = new Phaser(1);
		final Consumer<Department> nextConsumer = dep -> logger.info(String.format(
				"subscribeDepartments(): department name[%s], employees size[%d]", dep.name(), dep.employees().size()));
		final Consumer<Throwable> errorConsumer = exc -> {
			logger.error(String.format("subscribeDepartments(): exception[%s]", exc.getMessage()));
			phaser.forceTermination();
		};
		final Runnable completeConsumer = () -> {
			logger.debug("subscribeDepartments(): completed");
			phaser.arriveAndDeregister();
		};
		phaser.register();
		departmentsFlux.subscribe(nextConsumer, errorConsumer, completeConsumer);
		phaser.arriveAndAwaitAdvance();
	}

	/**
	 * Finds a department by the department key.
	 * 
	 * @param departmentKey the department key
	 */
	public void subscribeDepartment(String departmentKey) {

		final Mono<Department> departmentMono = client.get().uri(GET_DEPARTMENT_PATH, departmentKey)/*-*/
				.retrieve().bodyToMono(Department.class)/*-*/
				.transform(mono -> VERBOSE ? mono.log() : mono);

		departmentMono.blockOptional().ifPresentOrElse(
				dep -> logger
						.info(String.format("subscribeDepartment(): department key[%s], name[%s], employees size[%d]",
								departmentKey, dep.name(), dep.employees().size())),
				() -> logger.info("subscribeDepartment(): mono completed empty"));
	}

}
