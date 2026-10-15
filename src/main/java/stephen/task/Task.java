package stephen.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /** Strict ISO date format accepted in commands and used in saved task data. */
    public static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    /** Friendly date format used when displaying dated tasks to the user. */
    public static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);
    /** Fixed category that determines the task's display and storage symbol. */
    protected final TaskType type;
    /** User-provided text describing the task. */
    protected String description;
    /** Whether the user has completed the task. */
    protected boolean isDone;

    /**
     * Creates a task that is initially not done.
     *
     * @param type fixed type of the task
     * @param description description of the task
     */
    public Task(TaskType type, String description) {
        assert type != null : "A task must have a task type";
        assert description != null : "A task description must not be null";
        assert !description.isBlank() : "A task description must contain non-whitespace text";
        this.type = type;
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to display the task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the text describing this task. */
    public String getDescription() {
        return description;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Checks whether this task occurs on a date. Tasks without dates do not occur
     * on any schedule date; dated subclasses override this behavior.
     *
     * @param date date to check
     * @return whether the task occurs on the date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains the search phrase.
     *
     * @param keyword search phrase to match
     * @return whether the description contains the phrase, ignoring case
     */
    public boolean matches(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this task in the pipe-delimited format used in the save file.
     * Backslashes, pipes, and line breaks are escaped so each task stays on one line.
     *
     * @return serialized task data
     */
    public String toDataString() {
        return type.getSymbol() + " | " + (isDone ? "1" : "0") + " | "
                + escape(description);
    }

    /**
     * Escapes characters that have structural meaning in the save file.
     *
     * @param value text to escape
     * @return escaped text suitable for one save-file field
     */
    protected static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    /**
     * Returns the task in the format used by the chatbot.
     *
     * @return status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + description;
    }
}
