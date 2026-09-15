package ekud.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import ekud.EkudException;

/**
 * A calendar date, optionally with a time of day.
 * Date-only values are stored as {@link LocalDate}; values with a time are
 * the equivalent of {@link java.time.LocalDateTime}.
 * Invalid text is rejected rather than kept as a raw string.
 */
public final class TaskDateTime {
    private static final String USAGE =
            "Please use a date such as 2019-12-02 or 2/12/2019, optionally with a time (1800 or 18:00).";

    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HH:mm")
    };

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("d/M/yyyy")
    };

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter SAVE_TIME =
            DateTimeFormatter.ofPattern("HHmm");

    private final LocalDate date;
    private final LocalTime time;

    private TaskDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    /**
     * Parses a date or date-time. Supported forms include {@code yyyy-MM-dd},
     * {@code d/M/yyyy}, and those dates with {@code HHmm} or {@code HH:mm}.
     * {@code 2/12/2019} is 2 December 2019 (day/month).
     *
     * @param input text typed after {@code /by}, {@code /from}, {@code /to}, or {@code on}
     * @return the parsed date, with a time if the input included one
     * @throws EkudException if {@code input} is blank or does not match a supported format
     */
    public static TaskDateTime parse(String input) throws EkudException {
        if (input == null || input.isBlank()) {
            throw new EkudException(USAGE);
        }
        String trimmed = input.trim();
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(trimmed, formatter);
                return new TaskDateTime(dateTime.toLocalDate(), dateTime.toLocalTime());
            } catch (DateTimeParseException e) {
                // try the next pattern
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return new TaskDateTime(LocalDate.parse(trimmed, formatter), null);
            } catch (DateTimeParseException e) {
                // try the next pattern
            }
        }
        throw new EkudException(USAGE);
    }

    /**
     * Returns the calendar date, ignoring any time of day.
     *
     * @return the date
     */
    public LocalDate toLocalDate() {
        return date;
    }

    /**
     * Returns whether this value includes a time of day.
     *
     * @return {@code true} if a time was parsed
     */
    public boolean hasTime() {
        return time != null;
    }

    /**
     * Returns the time of day.
     *
     * @return the time, or {@code null} if this value is date-only
     */
    public LocalTime toLocalTime() {
        return time;
    }

    /**
     * Returns whether this value falls on {@code day}.
     *
     * @param day the calendar date to test
     * @return {@code true} if the dates are the same
     */
    public boolean occursOn(LocalDate day) {
        return date.equals(day);
    }

    /**
     * Returns {@code date} in the same display form as date-only tasks,
     * for example {@code Dec 02 2019}.
     *
     * @param date the calendar date to format
     * @return the display text
     */
    public static String toDisplayDate(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Returns a human-readable form such as {@code Dec 02 2019} or
     * {@code Dec 02 2019, 6:00 PM}.
     *
     * @return the display text
     */
    public String toDisplayString() {
        if (time == null) {
            return date.format(DISPLAY_DATE);
        }
        return date.format(DISPLAY_DATE) + ", " + time.format(DISPLAY_TIME);
    }

    /**
     * Returns the canonical text written to the save file and to
     * {@code toCommandString}, always {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm}.
     *
     * @return the save/reload text
     */
    public String toSaveString() {
        if (time == null) {
            return date.toString();
        }
        return date.toString() + " " + time.format(SAVE_TIME);
    }
}
