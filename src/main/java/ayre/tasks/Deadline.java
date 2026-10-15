package ayre.tasks;

import java.time.LocalDate;

/**
 * A TimedTask that only holds a single date.
 */
public class Deadline extends TimedTask {
    private final LocalDate dueDate;

    /**
     * Initializes Deadline by parsing the user input as a LocalDate.
     *
     * @param name Name of the Deadline.
     * @param dueDate ISO date due by.
     */
    public Deadline(String name, String dueDate) {
        super(name);
        this.dueDate = LocalDate.parse(dueDate);
    }

    /**
     * Converts the Task into a String that is easily parsable to store into the save file.
     *
     * @return String containing all necessary information about the Task to reconstruct it later.
     */
    @Override
    public String toLogString() {
        return "D " + super.toLogString() + " " + dueDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " <by: " + this.formatDate(dueDate) + ">";
    }
}
