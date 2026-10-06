package com.gihun456.model;

/**
 * Represents a task in the user's task list.
 * Supports todo, deadline, and event tasks.
 */
public class Task {
    private String taskName;
    private boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param taskName Description of the task.
     */
    public Task(String taskName) {
        assert taskName != null : "A task must have a non-null description";
        this.taskName = taskName;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return Description of the task.
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Returns the status icon for display.
     *
     * @return "X" when done and a blank space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return True if the task is marked as done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not completed.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "]" + " " + this.taskName;
    }
}
