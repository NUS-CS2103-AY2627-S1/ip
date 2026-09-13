package odysseus;

import java.util.Locale;

/**
 * A task recorded in Odysseus's voyage log.
 *
 * <p>A task owns its description and completion state so callers need not manage
 * the two pieces of information separately.</p>
 */
public abstract class Task {
    private final String description;
    private TaskStatus status;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task.
     */
    protected Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns whether this task has been completed.
     */
    protected boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /**
     * Returns this task's description.
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Returns whether this task's description contains the keyword, ignoring case.
     */
    protected boolean hasDescriptionContaining(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this task's one-letter type marker.
     */
    protected abstract String getTypeMarker();

    /**
     * Returns type-specific details for display.
     */
    protected String getDetails() {
        return "";
    }

    /**
     * Returns this task's type, status, description, and details for display.
     */
    @Override
    public final String toString() {
        return "[" + getTypeMarker() + "]" + status.getMarker() + " " + description + getDetails();
    }
}
