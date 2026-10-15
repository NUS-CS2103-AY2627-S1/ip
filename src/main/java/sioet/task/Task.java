package sioet.task;

/**
 * Represents one task in the task list.
 * A task has a description and can be marked as completed or incomplete.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        assert description != null : "Task description should not be null";
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
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} when the task is completed; otherwise {@code false}
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's description for subclasses that add task-specific details.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the one-letter code used to identify this kind of task.
     *
     * @return the task type code
     */
    protected abstract String getTaskType();

    /**
     * Returns any extra text displayed after the description.
     *
     * @return task-specific display details
     */
    protected String getDetails() {
        return "";
    }

    /**
     * Returns this task in the format used in task lists.
     *
     * @return the type marker, completion marker, description, and task details
     */
    @Override
    public String toString() {
        String status = isDone ? "X" : " ";
        return "[" + getTaskType() + "][" + status + "] " + description + getDetails();
    }
}
