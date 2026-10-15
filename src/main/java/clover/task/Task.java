package clover.task;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents the shared state and behavior of a task in the task list.
 */
public abstract class Task {
    private final String description;
    private final String tutoreeName;
    private TaskStatus status;

    /**
     * Creates an incomplete task with the supplied description.
     */
    protected Task(String description) {
        this(description, null);
    }

    /**
     * Creates an incomplete task with an optional linked tutoree.
     */
    protected Task(String description, String tutoreeName) {
        assert description != null : "Task descriptions must not be null.";
        this.description = description;
        this.tutoreeName = tutoreeName;
        this.status = TaskStatus.NOT_DONE;
    }

    private String getStatusIcon() {
        return status == TaskStatus.DONE ? "X" : " ";
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsUndone() {
        status = TaskStatus.NOT_DONE;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the linked tutoree name, or {@code null} when this task is not linked.
     */
    public String getTutoreeName() {
        return tutoreeName;
    }

    /**
     * Returns whether this task has been completed.
     */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /**
     * Returns whether another task represents the same user-entered task, excluding its completion status.
     */
    public boolean hasSameDetails(Task other) {
        if (other == null || getClass() != other.getClass()
                || !normalise(description).equals(normalise(other.description))
                || !Objects.equals(normalise(tutoreeName), normalise(other.tutoreeName))) {
            return false;
        }
        if (this instanceof Deadline deadline && other instanceof Deadline otherDeadline) {
            return deadline.getEndBy().equals(otherDeadline.getEndBy());
        }
        if (this instanceof Event event && other instanceof Event otherEvent) {
            return event.getStart().equals(otherEvent.getStart()) && event.getEnd().equals(otherEvent.getEnd());
        }
        return this instanceof ToDo && other instanceof ToDo;
    }

    /** Normalises text for duplicate comparison without changing its displayed form. */
    private String normalise(String text) {
        return text == null ? null : text.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /** Returns the common status and description portion of a task display. */
    protected String formatTaskDisplay() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /** Returns the optional display suffix identifying the linked tutoree. */
    protected String formatTutoreeSuffix() {
        return tutoreeName == null ? "" : " (for: " + tutoreeName + ")";
    }

    /**
     * Returns this task in Clover's display format.
     */
    @Override
    public String toString() {
        return formatTaskDisplay() + formatTutoreeSuffix();
    }
}
