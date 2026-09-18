package computah.task;

/**
 * Represents a task tracked by Computah.
 */
public class Task {
    /**
     * User-facing task description.
     */
    private String description;

    /**
     * Whether this task has been marked as done.
     */
    private boolean isDone;

    /**
     * Creates a task with the given description and a default not-done status.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return "X" for done tasks, or a blank space for not-done tasks.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the single-letter task type icon.
     *
     * @return task type icon.
     */
    public String getTaskType() {
        return "";
    }

    /**
     * Returns whether this task's description contains the given keyword.
     *
     * @param keyword keyword to search for.
     * @return true if the task description contains the keyword.
     */
    public boolean hasDescriptionContaining(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Converts this task into one line for the save file.
     *
     * @return save-file representation of this task.
     */
    public String toFileString() {
        return getTaskType() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the display representation of this task.
     *
     * @return task type, status, and description formatted for display.
     */
    @Override
    public String toString() {
        return "[" + getTaskType() + "][" + getStatusIcon() + "] " + description;
    }
}
