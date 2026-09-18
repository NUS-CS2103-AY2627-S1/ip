package rene.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task.
     * @param dueDate the date by which the task must be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns the date by which this task must be completed.
     *
     * @return the deadline value.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    Task copy() {
        Deadline copy = new Deadline(getDescription(), dueDate);
        if (isDone()) {
            copy.markAsDone();
        }
        return copy;
    }

    /**
     * Returns the deadline in the chatbot's display format.
     *
     * @return the basic task details followed by the deadline.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + dueDate.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
