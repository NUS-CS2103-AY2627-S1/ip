package mary.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Base class for all tasks.
 */
public class Task {
    // Strict resolution rejects impossible dates instead of adjusting their day.
    protected static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    protected static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("d MMM uuuu HH:mm");
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    protected String description;
    protected boolean isDone;

    /**
     * Creates an unfinished task with the given description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Checks only the description for a case-insensitive literal substring.
     *
     * @param keyword non-null search text.
     * @return whether the description contains the keyword, ignoring case.
     */
    public boolean matchesDescription(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Compares type, exact description, and scheduled dates, ignoring completion.
     */
    public boolean hasSameDetails(Task other) {
        if (getClass() != other.getClass() || !description.equals(other.description)) {
            return false;
        }
        if (this instanceof Deadline deadline && other instanceof Deadline otherDeadline) {
            return deadline.getBy().equals(otherDeadline.getBy());
        }
        if (this instanceof Event event && other instanceof Event otherEvent) {
            return event.getFrom().equals(otherEvent.getFrom()) && event.getTo().equals(otherEvent.getTo());
        }
        return true;
    }

    /**
     * Marks this task as unfinished.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Sets the completion state when loading a saved task.
     */
    public void setDone(boolean done) {
        isDone = done;
    }

    /**
     * Returns the portable record used for saving this task.
     */
    public String toStorageRecord() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Parses the date/time format accepted by MARY commands.
     */
    public static LocalDateTime parseDateTime(String value) {
        return LocalDateTime.parse(value, INPUT_DATE_TIME);
    }

    /**
     * Parses a date used by the date search command.
     */
    public static LocalDate parseDate(String value) {
        return LocalDate.parse(value, INPUT_DATE);
    }

    /**
     * Formats a date/time for display.
     */
    public static String formatDateTime(LocalDateTime value) {
        return value.format(DISPLAY_DATE_TIME);
    }

    /**
     * Returns the symbol used to display this task's status.
     *
     * @return {@code X} for a completed task, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task with its completion status.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}

//to rectify branching error
