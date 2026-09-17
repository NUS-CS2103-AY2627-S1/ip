package panda.task;

import java.time.temporal.Temporal;
import java.util.regex.Pattern;

/**
 * Represents a single item tracked by Panda.
 */
public abstract class Task {
    private final String description;
    private boolean isMarked;

    /**
     * Constructs an unmarked task with the given description.
     *
     * @param description Description of the task.
     */
    protected Task(String description) {
        assert description != null && !description.isBlank()
                : "Task description must not be null or blank";
        this.description = description;
        this.isMarked = false;
    }

    /**
     * Returns the description of this task.
     *
     * @return Description text.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks whether this task's description contains the specified whole keyword, ignoring letter case.
     *
     * @param keyword Keyword to find in the description.
     * @return {@code true} if the description contains the whole keyword; {@code false} otherwise.
     */
    public boolean hasKeyword(String keyword) {
        String keywordPattern = "(?<![\\p{L}\\p{N}])"
                + Pattern.quote(keyword)
                + "(?![\\p{L}\\p{N}])";
        return Pattern.compile(keywordPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
                .matcher(description)
                .find();
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return {@code true} if marked; {@code false} otherwise.
     */
    public boolean isMarked() {
        return isMarked;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isMarked = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isMarked = false;
    }

    /**
     * Checks if this task occurs on, is due on, or spans across the specified date.
     *
     * @param date Date to check against as a {@link Temporal}.
     * @return {@code true} if this task matches the specified date, {@code false} otherwise.
     */
    public abstract boolean occursOn(Temporal date);

    /**
     * Returns a string representation of the task.
     *
     * @return Formatted task string.
     */
    @Override
    public String toString() {
        return "[" + (isMarked ? "X" : " ") + "] " + description;
    }
}
