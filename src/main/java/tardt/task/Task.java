package tardt.task;

import static tardt.priority.Priority.LOW;

import tardt.priority.Priority;

/**
 * Encapsulates the description and status of a task.
 */
public class Task {
    private final String description;
    private boolean isDone;
    private final Priority priority;

    /**
     * Constructor for Task object
     * @param description Description of the task
     */
    public Task(String description) {
        this(description, LOW);
    }

    /**
     * Second constructor for Task Object including priority
     */
    public Task(String description, Priority priority) {
        assert description != null : "A task must have a description";
        assert priority != null : "A task must have a priority";
        this.description = description;
        this.priority = priority;
    }

    /**
     * Process the status icon of Task depending on whether it is done.
     * @return "X" if task is done, " " otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    public boolean isDone() {
        return isDone;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Marks the Task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks the Task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description + " | Priority: " + priority;
    }
}
