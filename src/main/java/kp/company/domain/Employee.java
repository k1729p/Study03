package kp.company.domain;

/**
 * The employee.
 * <p>
 * A domain object to be persisted to Redis.
 * </p>
 *
 * @param firstName the first name
 * @param lastName  the last name
 */
public record Employee(String firstName, String lastName) {

    /**
     * Creates an employee with names generated from the index.
     *
     * @param index the index
     * @return the employee
     */
    public static Employee fromIndex(int index) {
        return new Employee("EF-Name-%02d".formatted(index), "EL-Name-%02d".formatted(index));
    }

}
