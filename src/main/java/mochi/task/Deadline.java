package mochi.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be done before a specific date.
 */
public class Deadline extends Task {
    protected LocalDate by;

    /**
     * Creates a deadline task with the given description and due date.
     *
     * @param description the task description
     * @param by          the deadline date
     */
    public Deadline(String description, LocalDate by) {
        super(TaskType.DEADLINE, description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[" + type.getSymbol() + "]" + super.toString()
                + " (by: " + by.format(DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH)) + ")";
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | " + by.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
