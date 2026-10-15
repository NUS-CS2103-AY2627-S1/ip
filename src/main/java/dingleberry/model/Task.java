package dingleberry.model;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    /** Shared format used to display date/times, e.g. "Dec 2 2019, 6:00 pm". */
    public static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            new DateTimeFormatterBuilder()
                .appendPattern("MMM d yyyy, h:mm ")
                .appendText(ChronoField.AMPM_OF_DAY,
                    Map.of(0L, "am", 1L, "pm"))
                .toFormatter(Locale.ENGLISH);

    /** Stores the user-visible description. */
    private String description;
    /** Tracks whether this task is complete. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param taskDescription the user-visible description.
     */
    public Task(final String taskDescription) {
        this.description = taskDescription;
        this.isDone = false;
    }

    /**
     * Returns the completion-status icon displayed with this task.
     *
     * @return "X" when complete; otherwise, a space.
     */
    public final String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Marks this task as complete. */
    public final void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public final void unmarkDone() {
        isDone = false;
    }

    /**
     * Returns the user-visible description of this task.
     *
     * @return the task description.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is complete.
     *
     * @return true if this task is complete.
     */
    public final boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether two tasks have identical type, description, and dates.
     *
     * @param other the task to compare with this task.
     * @return true when the task content matches, regardless of completion.
     */
    public final boolean hasSameContentAs(final Task other) {
        if (other == null || !getClass().equals(other.getClass())) {
            return false;
        }
        String thisContent = toSaveFormat().replaceFirst(" \\| [01] \\| ",
                " | ");
        String otherContent = other.toSaveFormat().replaceFirst(
                " \\| [01] \\| ", " | ");
        return thisContent.equals(otherContent);
    }

    /**
    * Encodes the fields common to every task type as "doneFlag | description"
    * for use by subclasses building their {@link #toSaveFormat()} line.
    *
    * @return the encoded common task fields.
     */
    protected final String encodeCommonFields() {
        return String.format("%s | %s", isDone ? "1" : "0",
                escapeDescription(description));
    }

    /**
     * Escapes characters that would otherwise change the record structure.
     *
     * @param text the description to escape.
     * @return the escaped description.
     */
    private static String escapeDescription(final String text) {
        return text.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
        * Encodes this task as a single line for persistence in the data file.
        *
        * @return the persisted task representation.
     */
    public abstract String toSaveFormat();

    /**
     * Returns a user-facing representation. Subclasses should prepend their
     * type marker and include this representation.
     *
     * @return the status and description for this task.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.description);
    }
}
