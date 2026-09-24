package aravii;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a calendar date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline using a date object rather than display text.
     *
     * @param description The task description.
     * @param dueDate The date by which the task is due.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date must not be null.");
        }
        this.dueDate = dueDate;
    }

    /**
     * Returns the stored due date.
     *
     * @return The due date.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    TaskType getType() {
        return TaskType.DEADLINE;
    }

    @Override
    String formatDetails() {
        return "(by: " + dueDate.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    String formatStorageDetails() {
        return "(by: " + dueDate + ")";
    }
}
