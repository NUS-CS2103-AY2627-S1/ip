package nico;

import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /**
     * Date-time format shared by task display and saved task records.
     */
    static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");

    private static final String DONE_STATUS = "[X]";
    private static final String NOT_DONE_STATUS = "[ ]";

    private final String description;
    private boolean isDone;

    /**
     * Null until the user assigns a priority.
     */
    private Priority priority;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Task description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns whether this task has been marked as completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the display marker for this task's completion status.
     */
    protected String getStatusMarker() {
        String status = isDone ? DONE_STATUS : NOT_DONE_STATUS;
        return priority == null ? status : status + "[" + priority + "]";
    }

    /**
     * Returns the assigned priority, or null if none has been assigned.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the priority; null restores the unassigned state.
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Returns this task in the format used for display and storage.
     *
     * @return formatted task text, including its completion status
     */
    @Override
    public String toString() {
        return getStatusMarker() + " " + description;
    }
}
