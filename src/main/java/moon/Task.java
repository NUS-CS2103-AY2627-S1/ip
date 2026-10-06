package moon;

import java.util.Locale;

/**
 * Represents one task in Moon's task list.
 */
public class Task {
    /** The text that describes this task. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to show this task's completion status.
     *
     * @return {@code X} when the task is done; otherwise, a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has the same details as another task, ignoring completion status.
     *
     * @param other the task to compare with
     * @return whether both tasks have the same type and description
     */
    public boolean hasSameDetailsAs(Task other) {
        return other != null && getClass().equals(other.getClass()) && description.equals(other.description);
    }

    /**
     * Returns whether this task description contains the specified keyword,
     * ignoring letter case.
     *
     * @param keyword the keyword to search for
     * @return whether the description contains the keyword
     */
    public boolean matchesKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this task in the format used by the data file.
     *
     * @return a line that can be saved to Moon's data file
     */
    public String toSaveFormat() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void unmarkAsDone() {
        isDone = false;
    }
}
