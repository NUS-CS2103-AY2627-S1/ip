package panda.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import panda.exception.InvalidDateException;

/**
 * Represents one task in Panda's in-memory task list.
 *
 * Keeps a task's name and completion status together so that
 * status changes are handled by the task itself.
 */
public abstract class Task {
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);
    private String name;
    private TaskStatus status;


    /**
     * Creates a new unfinished task with the given name.
     *
     * @param name the text entered for the task.
     */
    public Task(String name) {
        this.name = name;
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the task's name.
     *
     * @return the task name.
     */
    public String getName() {
        return name;
    }

    /**
     * Changes this task's name.
     *
     * @param updatedName the validated replacement name.
     */
    public void rename(String updatedName) {
        assert updatedName != null && !updatedName.isBlank()
                : "A task name must not be blank.";
        name = updatedName;
    }

    /**
     * Returns whether the task has been completed.
     *
     * @return true when the task is done.
     */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as unfinished.
     */
    public void unmark() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns this task's type.
     *
     * Lets each task subtype supply its display and storage marker through
     * the corresponding enum value.
     *
     * @return the task type.
     */
    public abstract TaskType getType();

    /**
     * Returns the task text to show in confirmations and task lists.
     *
     * Provides a default display value that dated task
     * subclasses can extend with their own details.
     *
     * @return the formatted task description.
     */
    public String getDisplayText() {
        return name;
    }

    /**
     * Reports whether this task occurs on the supplied date.
     * Date-free task types return false unless a subclass overrides this method.
     *
     * @param date the date used to filter the task list.
     * @return true when this task occurs on {@code date}.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Converts this task into one line of Panda's storage format.
     *
     * Stores the common task type, status, and description
     * here so subclasses only need to append their additional fields.
     *
     * @return the pipe-separated representation of this task.
     */
    public String toDataString() {
        return getType().getMarker() + " | " + status.getStorageValue()
                + " | " + escapeDataField(name);
    }

    /**
     * Escapes characters that have special meaning in the storage format.
     *
     * @param value a task field supplied by the user.
     * @return the value with backslashes and pipe characters escaped.
     */
    protected static String escapeDataField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Parses a date and time using Panda's required input format.
     * Strict parsing rejects impossible values such as 29 February 2025.
     *
     * @param dateTimeText the date and time in {@code uuuu-MM-dd HH:mm} format.
     * @return the parsed date and time.
     * @throws InvalidDateException if the text is malformed or contains an invalid value.
     */
    protected static LocalDateTime parseDateTime(String dateTimeText)
            throws InvalidDateException {
        try {
            return LocalDateTime.parse(dateTimeText, INPUT_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new InvalidDateException();
        }
    }

    /**
     * Parses the optional date supplied to the list command.
     *
     * @param dateText the date in {@code uuuu-MM-dd} format.
     * @return the parsed date.
     * @throws InvalidDateException if the date is malformed or impossible.
     */
    public static LocalDate parseListDate(String dateText)
            throws InvalidDateException {
        try {
            return LocalDate.parse(dateText, INPUT_DATE_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw InvalidDateException.createForListDate();
        }
    }

    /**
     * Formats a parsed value for confirmations and task lists.
     *
     * @param dateTime the value to format.
     * @return the value in {@code MMM dd uuuu HH:mm} format.
     */
    protected static String formatDateTimeForDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_DATE_TIME_FORMATTER);
    }

    /**
     * Formats a parsed value in Panda's unambiguous storage format.
     *
     * @param dateTime the value to format.
     * @return the value in {@code uuuu-MM-dd HH:mm} format.
     */
    protected static String formatDateTimeForStorage(LocalDateTime dateTime) {
        return dateTime.format(INPUT_DATE_TIME_FORMATTER);
    }
}
