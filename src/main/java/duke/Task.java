package duke;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * Represents one task in Furina's in-memory task list.
 */
public class Task {
    protected TaskType type;

    protected String description;

    protected boolean isDone;

    protected String by;

    protected LocalDateTime byDateTime;

    protected String from;

    protected String to;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this(TaskType.TODO, description, null, null, null);
    }

    /**
     * Creates a task with a type and optional deadline or event details.
     *
     * @param type the task type
     * @param description the text describing the task
     * @param by the deadline text, if this is a deadline task
     * @param from the event start text, if this is an event task
     * @param to the event end text, if this is an event task
     */
    public Task(TaskType type, String description, String by, String from, String to) {
        this.type = type;
        this.description = description;
        this.isDone = false;
        this.by = by;
        this.byDateTime = null;
        this.from = from;
        this.to = to;
    }

    /**
     * Creates a deadline task whose deadline is represented as a date and time.
     *
     * @param description the text describing the task
     * @param byDateTime the parsed deadline
     */
    public Task(String description, LocalDateTime byDateTime) {
        this(TaskType.DEADLINE, description, null, null, null);
        this.byDateTime = byDateTime;
    }

    /**
     * Returns the status marker used when displaying this task.
     *
     * @return X when done, or a blank space when not done
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Checks whether two tasks have the same type and user-visible details.
     *
     * @param other the task to compare with this task
     * @return true when both tasks represent the same task details
     */
    boolean hasSameDetails(Task other) {
        return other != null
                && type == other.type
                && Objects.equals(description, other.description)
                && Objects.equals(by, other.by)
                && Objects.equals(byDateTime, other.byDateTime)
                && Objects.equals(from, other.from)
                && Objects.equals(to, other.to);
    }

    /**
     * Returns a comparator that orders tasks by parsed deadline, leaving
     * tasks without a parsed deadline after dated deadlines.
     *
     * @return a comparator for chronological deadline ordering
     */
    static Comparator<Task> byDeadline() {
        return Comparator.comparing(task -> task.byDateTime,
                Comparator.nullsLast(Comparator.naturalOrder()));
    }

    /**
     * Returns the task in the format used by the chatbot.
     *
     * @return the status marker and task description
     */
    @Override
    public String toString() {
        String details = switch (type) {
        case TODO -> "";
        case DEADLINE -> " (by: " + (byDateTime == null ? by : DateTimeParser.format(byDateTime)) + ")";
        case EVENT -> " (from: " + from + " to: " + to + ")";
        };
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] "
                + description + details;
    }
}
