package doge.model;

/** Stores the description and completion status shared by all task types. */
public class Task {
    /** Stores the user-facing task description. */
    protected final String description;
    /** Stores whether this task has been completed. */
    protected boolean isDone;
    private int priority;

    /** Creates an incomplete task with the given description. */
    public Task(String description) {
        if (description == null) {
            throw new IllegalArgumentException("A task must have a non-null description");
        }
        this.description = description;
        this.isDone = false;
        this.priority = 0;
    }

    /** Returns {@code X} for a completed task and a blank space otherwise. */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Returns the task in the format displayed by the user interface. */
    @Override
    public String toString() {
        String priorityLabel = priority == 0 ? "" : "[P" + priority + "]";
        return "[" + getStatusIcon() + "]" + priorityLabel + " " + description;
    }

    /** Marks this task as completed. */
    public void markDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmarkDone() {
        this.isDone = false;
    }

    /** Returns this task's description. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task is completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns this task's priority level, where zero means no priority is set. */
    public int getPriority() {
        return priority;
    }

    /** Sets this task's priority level. A level of zero clears the priority. */
    public void setPriority(int priority) {
        if (priority < 0) {
            throw new IllegalArgumentException("A task priority must not be negative");
        }
        this.priority = priority;
    }

    /** Returns this task in the pipe-delimited format used for persistence. */
    public String toStorageString() {
        String status = isDone ? "1" : "0";
        String taskData = "T | " + status + " | " + description;
        return priority == 0 ? taskData : taskData + " | " + priority;
    }

}
