package kp.company.handlers.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * The base class for the handler tests.
 */
@SpringBootTest
public class HandlersTestsBase {
    /**
     * The {@link RouterFunction}.
     */
    @Autowired
    protected RouterFunction<ServerResponse> routerFunction;

    /**
     * The {@link WebTestClient}.
     */
    protected WebTestClient webTestClient;

    /**
     * Executed before each test.
     */
    @BeforeEach
    void setup() {
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

}
