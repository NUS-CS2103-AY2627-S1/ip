package hu9o.task;

/**
 * Represents a task in Hu9o's todo list.
 */
public abstract class Task {
    private final String description;
    private final String taskType;
    private boolean isDone = false;

    /**
     * Creates an unfinished task of the specified type.
     *
     * @param description the task's description.
     * @param taskType    the one-letter task type shown in the task output.
     */
    protected Task(String description, String taskType) {
        // taskType is always a one-letter literal ("T"/"D"/"E") from a subclass;
        // compressionString(), toString(), and Parser.parseTask() all rely on that.
        assert taskType != null && taskType.length() == 1
                : "taskType must be a single-letter code";
        this.description = description;
        this.taskType = taskType;
    }

    public String getTaskType() {
        return this.taskType;
    }

    public String getTaskDescription() {
        return this.description;
    }

    /**
     * Returns the one-character status icon: {@code "X"} when done, a space otherwise.
     *
     * @return the status icon.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been marked done.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        isDone = true;
    }

    /**
     * Marks this task as unfinished.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Provides the type-specific text shown after the task description.
     *
     * @return the formatted task specification, including its parentheses.
     */
    protected abstract String specificationSuffix();

    /**
     * Returns this task as a pipe-delimited record for the save file,
     * such as {@code T|X|read book|}.
     *
     * @return the persistence record for this task.
     */
    public String compressionString() {
        return taskType + "|" + getStatusIcon() + "|" + description + "|";
    }

    @Override
    public String toString() {
        return "[" + taskType + "][" + getStatusIcon() + "] " + description + specificationSuffix();
    }
}
