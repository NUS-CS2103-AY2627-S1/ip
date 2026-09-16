package crow.task;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mma", Locale.ENGLISH);

    private final String description;
    private boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the symbol used to display the completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task description.
     *
     * @return Description of this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task has been completed.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Checks whether another task has the same type and description.
     * Completion status is intentionally ignored.
     *
     * @param other Task to compare.
     * @return {@code true} if both tasks have the same identifying details.
     */
    public boolean hasSameDetailsAs(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns the task in its display format.
     *
     * @return Status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
