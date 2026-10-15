package rocky.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents the common state and behavior of all task types.
 */
public abstract class Task {
    protected static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    protected static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns this task's description for subclasses that format the task.
     *
     * @return the task description
     */
    protected String getDescription() {
        return description;
    }

    /** Returns whether this task description contains a keyword.
     *
     * @param keyword the case-insensitive text to search for.
     * @return {@code true} when the description contains the keyword.
     */
    public boolean matchesKeyword(String keyword) {
        return keyword != null && description.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }

    /** Returns the date used when tasks are sorted, if this task has one.
     *
     * @return the task's deadline or start date, or an empty value for undated tasks.
     */
    public abstract Optional<LocalDateTime> getSortDate();

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return {@code "X"} when the task is done, otherwise a space
     */
    protected String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's completion status in the persistence format.
     *
     * @return {@code "1"} when done, otherwise {@code "0"}
     */
    protected String getStatusValue() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns a line that can be written to disk and parsed later.
     *
     * @return the task's storage representation
     */
    public abstract String toStorageString();

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
}
