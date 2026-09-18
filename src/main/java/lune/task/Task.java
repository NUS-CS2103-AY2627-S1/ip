package lune.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a description and a done/not-done status.
 * Subclasses ({@link Todo}, {@link Deadline}, {@link Event}) add the
 * type-specific date/time information and prepend their type tag.
 */
public class Task {
    /** Display format for a bare date, e.g. "Oct 15 2019". */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    /** Display format for a date with a time attached, e.g. "Oct 15 2019, 6:00 PM". */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    protected String description;
    protected boolean isDone;

    /**
     * Creates a new, not-done task with the given description.
     */
    public Task(String description) {
        // Every current call site (Lune.processCommand for live user input,
        // Lune.parseSavedTask for the save file) already rejects a
        // null/blank description before constructing a task, so this
        // documents that assumption rather than re-validating it — a
        // violation here would mean a new caller forgot that check, not
        // that the user typed something bad.
        assert description != null && !description.isBlank() : "a task's description must never be null or blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns this task's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the single-character icon shown for this task's done status:
     * "X" if done, a space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Renders this task for display, e.g. "[X] read book". Subclasses
     * prepend their type tag and append their own date/time fields.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Renders this task as one line of the on-disk save format, e.g.
     * "1 | read book". Subclasses prepend their type letter and append
     * their own fields.
     */
    public String toSaveFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns whether this task is associated with the given date. Todos
     * never are; Deadline/Event override this with their own date(s). Used
     * by the "on <date>" command.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Formats a date/time for display: just the date (e.g. "Oct 15 2019")
     * if no time-of-day was given (midnight), or date and time together
     * (e.g. "Oct 15 2019, 6:00 PM") otherwise. A task created from a plain
     * "yyyy-mm-dd" input has no way to distinguish "no time given" from
     * "genuinely means midnight" — this treats midnight as "no time given",
     * which matches every realistic use of this chatbot.
     */
    protected static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE_FORMAT);
        }
        return dateTime.format(DISPLAY_DATE_TIME_FORMAT);
    }

    /**
     * Formats a bare date the same way task dates are displayed, e.g.
     * "Oct 15 2019". Used outside this package (e.g. by the "on <date>"
     * command) to render the queried date consistently with how tasks
     * render their own dates, without exposing the formatter itself.
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMAT);
    }
}
