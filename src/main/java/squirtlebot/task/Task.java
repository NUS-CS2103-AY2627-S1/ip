package squirtlebot.task;

import java.io.Serializable;

/**
 * Provides a base implementation for tasks used in {@code SquirtleBot}.
 * Contains common methods used by other subclasses of task.
 */
public abstract class Task implements Serializable {
    private String taskDescription;
    private Boolean isDone;

    /**
     * Constructs a new task with a task description.
     * This constructor is intended to be used by subclasses of task to set task description.
     *
     * @param taskDescription a description of the task.
     */
    public Task(String taskDescription) {
        this.taskDescription = taskDescription.trim();
        isDone = false;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setDone(Boolean isDone) {
        this.isDone = isDone;
    }

    public boolean isDone() {
        return isDone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof Task otherTask) {
            return this.taskDescription.equals(otherTask.taskDescription)
                    && this.isDone.equals(otherTask.isDone);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", isDone ? "X" : " ", taskDescription);
    }
}
