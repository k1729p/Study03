package kp.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Reactive REST Web Service server application with the Redis.<br/>
 * The Redis is the in-memory data store.
 *
 */
@SpringBootApplication
public class Application {

	/**
	 * The constructor.
	 */
	public Application() {
		super();
	}

	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}