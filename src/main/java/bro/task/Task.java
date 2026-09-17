package bro.task;

/** Represents a task and its completion status. */
public class Task {
    private final String description;
    private boolean isDone;
    private final TaskType taskType;

    /** Creates a basic task, treated as a todo for display purposes. */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /** Creates a task with the given description and category. */
    protected Task(String description, TaskType taskType) {
        this.description = description;
        this.isDone = false;
        this.taskType = taskType;
    }

    /** Returns X for a completed task and a blank space otherwise. */
    public String showDone() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Sets whether this task has been completed. */
    public void setDone(boolean done) {
        isDone = done;
    }

    /** Returns the task description for collaborators in other packages. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Formats the task category, completion state, and description. */
    protected String formatTask(String taskDetails) {
        return "[" + taskType.getDisplayCode() + "] [" + showDone() + "] " + taskDetails;
    }

    /** Returns the task in the format shown by Bro's list command. */
    @Override
    public String toString() {
        return formatTask(description);
    }
}
