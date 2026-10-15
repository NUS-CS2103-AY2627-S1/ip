package lebron;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a deadline task that must be completed by a specific date.
 */
public class Deadline extends Task {

    protected LocalDate by;

    /**
     * Creates a new deadline task with the given description and due date.
     *
     * @param description description of the deadline task
     * @param by due date in yyyy-MM-dd format
     */
    public Deadline(String description, String by) {
        super(description);
        this.type = TaskType.DEADLINE;
        this.by = LocalDate.parse(by);
    }

    /**
     * Returns the string representation of the deadline task.
     *
     * @return formatted deadline task including its due date
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM dd yyyy");

        return "[D]" + super.toString()
                + " (by: " + by.format(formatter) + ")";
    }
}
