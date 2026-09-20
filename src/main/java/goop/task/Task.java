package goop.task;

import java.util.Objects;

/**
 * Represents a task with its completion state and optional priority.
 */
public class Task {
    /** Text describing the task. */
    private final String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /** Unassigned tasks have no priority marker. */
    private Priority priority = Priority.NONE;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the text describing this task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Compares task details, ignoring completion and priority so status changes
     * cannot make a duplicate task appear new. Text comparisons are case-sensitive.
     *
     * @param other Task to compare with this task.
     * @return True when type, description, and schedule match.
     */
    public boolean hasSameDetails(Task other) {
        if (other == null || getClass() != other.getClass()
                || !description.replaceAll("(?U)\\s+", " ").strip()
                        .equals(other.description.replaceAll("(?U)\\s+", " ").strip())) {
            return false;
        }
        if (this instanceof Deadline deadline) {
            return deadline.getBy().equals(((Deadline) other).getBy());
        }
        if (this instanceof Event event) {
            Event otherEvent = (Event) other;
            return EventSchedule.hasSameTime(event.getFrom(), otherEvent.getFrom())
                    && EventSchedule.hasSameTime(event.getTo(), otherEvent.getTo());
        }
        return true;
    }

    /**
     * Checks whether this task has been completed.
     *
     * @return True when the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = Objects.requireNonNull(priority);
    }

    /**
     * Returns the icon used to display the task's completion status.
     *
     * @return {@code X} when done, or a space when not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
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
     * Returns the task in the format used by the chatbot.
     *
     * @return The status icon, optional priority marker, and task description.
     */
    @Override
    public String toString() {
        String priorityMarker = priority == Priority.NONE ? "" : "[" + priority.getLabel() + "] ";
        return "[" + getStatusIcon() + "] " + priorityMarker + description;
    }
}
