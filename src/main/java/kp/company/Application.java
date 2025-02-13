package kp.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Reactive REST Web Service server application with Redis.
 * <p>
 * Redis is the in-memory data store.
 * </p>
 */
@SpringBootApplication
public class Application {
    /**
     * The primary entry point for launching the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}