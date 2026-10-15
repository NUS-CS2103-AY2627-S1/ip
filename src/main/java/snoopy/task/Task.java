package snoopy.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Text describing the task.
     */
    public Task(String description) {
        assert description != null && !description.isBlank() : "Task description must not be blank";
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
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Checks whether this task has been completed.
     *
     * @return {@code true} when the task is marked as done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the symbol used to display this task's completion state.
     *
     * @return {@code X} when completed, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Replaces this task's description while keeping its type and other details unchanged.
     *
     * @param description New text describing the task.
     */
    public void updateDescription(String description) {
        assert description != null && !description.isBlank() : "Task description must not be blank";
        this.description = description;
    }

    /**
     * Checks whether this task's description contains the given keyword.
     *
     * @param keyword Keyword to search for.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        assert keyword != null : "Search keyword must be provided";
        return description.contains(keyword);
    }

    /**
     * Converts the task's common fields into the format used in the data file.
     *
     * @return Completion status and description separated by {@code |}.
     */
    public String toDataString() {
        String status;
        if (this.isDone) {
            status = "1";
        } else {
            status = "0";
        }
        return status + " | " + this.description;
    }

    /**
     * Returns this task in the format shown by the chatbot.
     *
     * @return The status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
