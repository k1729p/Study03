package kp.company.domain;

/**
 * The employee.<br>
 * A domain object to be persisted to the Redis.
 * 
 * @param firstName the first name
 * @param lastName  the last name
 */
public record Employee(String firstName, String lastName) {

	/**
	 * Creates the employee with names generated from index.
	 * 
	 * @param index the index
	 * @return the employee
	 */
	public static Employee fromIndex(int index) {
		return new Employee(String.format("EF-Name-%2d", index), String.format("EL-Name-%2d", index));
	}

}
