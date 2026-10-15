package cheecken;

import java.util.Locale;

/**
 * Represents a task description and its completion state.
 */
public class Task {
    protected final String task;
    protected boolean isMarked;

    /**
     * Creates an unmarked task with the supplied description.
     * @param task description of the task
     */
    public Task(String task) {
        this.task = task;
        this.isMarked = false;
    }

    /**
     * Marks this task as completed.
     */
    public void mark() {
        this.isMarked = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void unmark() {
        this.isMarked = false;
    }

    /**
     * Returns whether this task description contains the supplied keyword.
     * @param keyword keyword to match against task descriptions
     * @return true if the description contains the keyword, ignoring case
     */
    public boolean containsKeyword(String keyword) {
        return task.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Serializes this task for persistence.
     * @return task record formatted for persistence
     */
    public String toStorageString() {
        return "T | " + (isMarked ? "1" : "0") + " | " + task;
    }

    /**
     * Returns the display representation of this task.
     * @return formatted task description and completion state
     */
    @Override
    public String toString() {
        return String.format("[" + (this.isMarked ? "X" : " ") + "] " + this.task);
    }
}
