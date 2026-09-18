package plana.task;

import java.util.Locale;

import plana.storage.Storage;

/**
 * Represents a task in Plana's task list.
 */
public class Task {
    protected String description;
    protected CompletionStatus completionStatus;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the task description.
     */
    public Task(String description) {
        // Task creation is preceded by parser/storage validation, so every task
        // can rely on having text that is meaningful to display and search.
        assert description != null && !description.isBlank()
                : "A task must have a non-blank description.";
        this.description = description;
        this.completionStatus = CompletionStatus.NOT_DONE;
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise
     */
    public String getStatusIcon() {
        // The completion status is assigned only from CompletionStatus values.
        assert completionStatus != null : "A task must always have a completion status.";
        return completionStatus.getStatusIcon();
    }

    /**
     * Checks whether this task's description contains a keyword.
     *
     * @param keyword the keyword to search for.
     * @return {@code true} when the description contains the keyword,
     *         ignoring letter case
     */
    public boolean matchesKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        completionStatus = CompletionStatus.DONE;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        completionStatus = CompletionStatus.NOT_DONE;
    }

    /**
     * Checks whether this task is marked as complete.
     *
     * @return true when this task is complete.
     */
    public boolean isDone() {
        return completionStatus == CompletionStatus.DONE;
    }

    /**
     * Checks whether this task has the same user-supplied details as another task.
     * Completion status is intentionally excluded because it does not identify
     * the task itself.
     *
     * @param otherTask the task to compare.
     * @return true when both tasks have the same type and details.
     */
    public boolean hasSameDetails(Task otherTask) {
        return otherTask != null && getDetailsKey().equals(otherTask.getDetailsKey());
    }

    /**
     * Returns a key made from the parts of this task that identify it.
     *
     * @return a task-type and description key.
     */
    protected String getDetailsKey() {
        return "T\u0000" + description;
    }

    /**
     * Returns the task in the format used by the storage file.
     *
     * @return the task type, completion status, and description
     */
    public String toStorageString() {
        return "T | " + getStorageStatus() + " | " + Storage.escapeField(description);
    }

    /**
     * Returns this task's completion status in the storage format.
     *
     * @return {@code 1} for done, or {@code 0} otherwise
     */
    protected String getStorageStatus() {
        return completionStatus == CompletionStatus.DONE ? "1" : "0";
    }

    /**
     * Returns the task in the format used when displaying the task list.
     *
     * @return the task status and description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
