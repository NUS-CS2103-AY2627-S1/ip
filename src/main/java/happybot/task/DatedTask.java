package happybot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that carries one or more dates.
 *
 * <p>Deadline and Event show their dates the same way, so the patterns and the formatting live
 * here. ToDo has no date, so it extends Task directly and never sees any of them.
 */
public abstract class DatedTask extends Task {
    /** Pattern used whenever a task date is shown, such as Oct 15 2019. */
    protected static final String DISPLAY_FORMAT = "MMM dd yyyy";

    /** Pattern added after the date when the task carries a time, such as 6:00PM. */
    protected static final String DISPLAY_TIME_FORMAT = "h:mma";

    /**
     * Creates a dated task with the specified description.
     *
     * @param description The task description.
     */
    protected DatedTask(String description) {
        super(description);
    }

    /**
     * Returns the text used to show a date on its own.
     *
     * <p>This is public because HappyBot writes a date of its own when it lists the deadlines
     * due on one day, and reusing this keeps one source of truth for how a date is written.
     *
     * @param date The date to display.
     * @return The date written in the display pattern, such as Oct 15 2019.
     */
    public static String formatDate(LocalDate date) {
        // Locale.ENGLISH keeps the month name the same on every machine.
        return date.format(DateTimeFormatter.ofPattern(DISPLAY_FORMAT, Locale.ENGLISH));
    }

    /**
     * Returns the text used to show a task date to the user.
     *
     * <p>The time is always shown, so a date given without one reads as 12:00AM.
     *
     * @param dateTime The date and time to display.
     * @return The date followed by the time.
     */
    protected static String formatDate(LocalDateTime dateTime) {
        // Locale.ENGLISH keeps the AM/PM marker the same on every machine.
        String time = dateTime.format(DateTimeFormatter.ofPattern(DISPLAY_TIME_FORMAT, Locale.ENGLISH));
        return formatDate(dateTime.toLocalDate()) + " " + time;
    }
}
