package chim.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that needs to be completed by a specific date/time.
 */
public class Deadline extends Task {

    protected LocalDate by;

    /**
     * Creates a Deadline with the given description and due date/time.
     *
     * @param description Description of the deadline task.
     * @param by Date/time the task is due by.
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + by;
    }

    @Override
    protected String getDetailsKey() {
        return super.getDetailsKey() + "|" + by;
    }

    @Override
    public String toString() {
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("MMM dd yyyy");
        return "[D]" + super.toString() + " (by: " + by.format(outputFormat) + ")";
    }
}
