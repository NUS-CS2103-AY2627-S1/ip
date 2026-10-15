package jarvis.classes;

import java.io.Serializable;

/** Represents a task that can be marked as done or undone. */
public class Task implements Serializable {
    /** Declares the version identifier for Java serialization to verify class compatibility. */
    private static final long serialVersionUID = 1L;
    /** Stores the task description. */
    private String taskName;
    /** Stores the task's completion status. */
    private CompletionStatus completedStatus;

    /** Represents the possible completion states of a task. */
    public enum CompletionStatus {
        DONE,
        UNDONE
    }


    /**
     * Creates a task with an initially undone status.
     *
     * @param taskName Description of the task.
     */
    public Task(String taskName) {
        this.taskName = taskName;
        this.completedStatus = CompletionStatus.UNDONE;
    }

    /**
     * Sets the completion status of this task.
     *
     * @param status New completion status.
     */
    public void setCompletionStatus(CompletionStatus status) {
        this.completedStatus = status;
    }

    /**
     * Returns the completion status of this task.
     *
     * @return Current completion status of this task.
     */
    public CompletionStatus getStatus() {
        return completedStatus;
    }

    /**
     * Returns the description of this task.
     *
     * @return Description of this task.
     */
    public String getTaskName() {
        return taskName;
    }


    /**
     * Returns the task in the format used by the task list.
     *
     * @return Formatted task description and completion marker.
     */
    @Override
    public String toString() {
        if (completedStatus == CompletionStatus.DONE) {
            return "[X] " + taskName;
        } else {
            return "[] " + taskName;
        }
    }
}
