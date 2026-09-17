package meow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a deadline task with a due date.
 */
public class Deadline extends Task {
    static final String STORAGE_TYPE = "D";
    protected LocalDate dueDate;


    /**
     * Creates a deadline task with the specified description and due date.
     *
     * @param description the description of the task
     * @param dueDate the due date of the task
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
        return "[D][" + super.getStatusForDisplay() + "] "
                + super.getDescription()
                + " (by: " + dueDate.format(formatter) + ")";
    }

    @Override
    public String toFileString() {
        return STORAGE_TYPE + " | " + super.getStatusForFile() + " | " + super.getDescription() + " | " + dueDate;
    }
}
