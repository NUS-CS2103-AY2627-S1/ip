package bloop.task;

import java.time.LocalDate;

/**
 * Represents a task with a completion status and priority.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    private final Priority priority;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this(description, Priority.LOW);
    }

    /** Creates an incomplete task with the given description and priority. */
    public Task(String description, Priority priority) {
        this.description = description;
        this.isDone = false;
        this.priority = priority;
    }

    /**
     * Returns the completion icon displayed beside this task.
     *
     * @return {@code X} when complete, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task description for storage or display.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return whether the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /** Returns this task's priority. */
    public Priority getPriority() {
        return priority;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /** Returns whether this task occurs on the supplied date. */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /** Returns whether this task description contains the supplied keyword or its priority matches it. */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword) || priority.getLabel().equalsIgnoreCase(keyword);
    }

    @Override
    public String toString() {
        return appendPriority("[" + getStatusIcon() + "] " + description);
    }

    /** Appends this task's priority to a task display string. */
    protected String appendPriority(String taskDisplay) {
        return taskDisplay + " (priority: " + priority.getLabel() + ")";
    }
}
