package kp.client.subscribers;

import kp.company.domain.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Phaser;
import java.util.function.Consumer;

import static kp.Constants.GET_DEPARTMENTS_PATH;
import static kp.Constants.GET_DEPARTMENT_PATH;

/**
 * The subscriber for the {@link Department}s.
 */
public class DepartmentSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final boolean VERBOSE = false;

    private final WebClient client;

    /**
     * Constructor.
     *
     * @param client the {@link WebClient}
     */
    public DepartmentSubscriber(WebClient client) {
        this.client = client;
    }

    /**
     * Subscribes to all {@link Department}s.
     */
    public void subscribeDepartments() {

        final Flux<Department> departmentsFlux = client.get().uri(GET_DEPARTMENTS_PATH).retrieve()
                .bodyToFlux(Department.class)
                .transform(flux -> VERBOSE ? flux.log() : flux);

        final Phaser phaser = new Phaser(1);
        final Consumer<Department> nextConsumer = dep -> logger.info(
                "subscribeDepartments(): department name[{}], employees size[{}]", dep.name(), dep.employees().size());
        final Consumer<Throwable> errorConsumer = exc -> {
            logger.error("subscribeDepartments(): exception[{}]", exc.getMessage());
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
     * Subscribes to a {@link Department} by the department key.
     *
     * @param departmentKey the key of the {@link Department}
     */
    public void subscribeDepartment(String departmentKey) {

        final Mono<Department> departmentMono = client.get().uri(GET_DEPARTMENT_PATH, departmentKey)
                .retrieve().bodyToMono(Department.class)
                .transform(mono -> VERBOSE ? mono.log() : mono);

        departmentMono.blockOptional().ifPresentOrElse(
                dep -> logger.info("subscribeDepartment(): department key[{}], name[{}], employees size[{}]",
                        departmentKey, dep.name(), dep.employees().size()),
                () -> logger.info("subscribeDepartment(): mono completed empty"));
    }

}
