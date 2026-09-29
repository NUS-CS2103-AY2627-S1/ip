package yokohama.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task with a description and completion state.
 */
public abstract class Todo {
    private static final String COMPLETED_STORAGE_VALUE = "1";
    private static final String INCOMPLETE_STORAGE_VALUE = "0";
    private static final DateTimeFormatter EVENT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM/dd/yyyy HHmm");
    private static final DateTimeFormatter USER_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

    private final String description;
    private boolean isDone;

    Todo(String description, boolean isDone) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Marks this task as complete.
     *
     * @return {@code true} if the task was marked complete, or {@code false} if it already was complete.
     */
    public boolean markComplete() {
        if (isDone) {
            return false;
        }

        isDone = true;
        return true;
    }

    /**
     * Marks this task as incomplete.
     *
     * @return {@code true} if the task was marked incomplete, or {@code false} if it already was incomplete.
     */
    public boolean unmarkComplete() {
        if (!isDone) {
            return false;
        }

        isDone = false;
        return true;
    }

    protected final String getDescription() {
        return description;
    }

    protected final boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's completion state in the format used by the data file.
     *
     * @return {@code "1"} for a completed task, otherwise {@code "0"}.
     */
    protected final String getStorageCompletionValue() {
        return isDone ? COMPLETED_STORAGE_VALUE : INCOMPLETE_STORAGE_VALUE;
    }

    protected static String convertFromLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(EVENT_DATE_TIME_FORMATTER);
    }

    protected static String formatForUser(LocalDateTime dateTime) {
        return dateTime.format(USER_DATE_TIME_FORMATTER);
    }

    /**
     * Returns whether this task's description contains the specified search keyword.
     * Matching is case-insensitive.
     *
     * @param keyword Keyword to search for.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        return this.description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns whether this task is scheduled on the specified date.
     * Ordinary tasks have no scheduled date.
     *
     * @param date Date to check.
     * @return {@code true} if this task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns this task in the format used by the data file.
     *
     * @return Serialized task representation.
     */
    public abstract String toDbString();

    @Override
    public String toString() {
        return isDone ? String.format("[ X ] %s", description) : String.format("[  ] %s", description);
    }
}
