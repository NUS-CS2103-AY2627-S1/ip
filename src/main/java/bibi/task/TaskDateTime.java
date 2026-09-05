package bibi.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import bibi.BibiException;

/**
 * A date, optionally with a time of day, attached to a deadline or an event.
 *
 * <p>Keeping the value as a {@link LocalDate} and {@link LocalTime} rather than
 * as plain text lets Bibi compare dates, which is what the {@code on} command
 * needs. The time is optional so that {@code /by 2019-10-15} stays a whole-day
 * deadline instead of silently becoming midnight.
 *
 * <p>Instances are immutable, so a task can share one safely.
 */
public class TaskDateTime {
    /** English is fixed so month names read the same on every machine. */
    private static final Locale FORMAT_LOCALE = Locale.ENGLISH;

    /** Shown to the user, for example {@code Oct 15 2019}. */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", FORMAT_LOCALE);

    /** Shown to the user after the date, for example {@code 6:00PM}. */
    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("h:mma", FORMAT_LOCALE);

    /**
     * Input formats that include a time of day, tried before the date-only
     * formats so that {@code 2/12/2019 1800} is not mistaken for a bad date.
     */
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS =
            strictFormats("uuuu-MM-dd HHmm", "d/M/uuuu HHmm");

    /** Input formats that give a date only. The first is also the saved form. */
    private static final List<DateTimeFormatter> DATE_FORMATS =
            strictFormats("uuuu-MM-dd", "d/M/uuuu");

    /** Guidance repeated wherever a date fails to parse. */
    private static final String FORMAT_HELP =
            "Use yyyy-MM-dd or d/M/yyyy, optionally followed by a 24-hour time, "
            + "for example 2019-10-15 or 2/12/2019 1800.";

    private final LocalDate date;

    /** The time of day, or {@code null} when only a date was supplied. */
    private final LocalTime time;

    /**
     * Creates a date, with or without a time of day.
     *
     * @param date the calendar date
     * @param time the time of day, or {@code null} for a whole-day value
     */
    private TaskDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    /**
     * Builds formatters that refuse dates which do not exist.
     *
     * <p>Java's default resolver quietly moves an impossible date such as
     * 2019-02-30 back to the last real day of the month. Strict resolving
     * rejects it instead, which is what a user who mistyped a date needs to see.
     * Strict resolving also requires {@code uuuu} for the year rather than
     * {@code yyyy}, because the latter is the year within an era.
     *
     * <p>Takes the patterns as varargs so that a group of accepted formats is
     * declared as the list of patterns it really is, rather than as a list of
     * separately wrapped calls.
     *
     * @param patterns the date patterns to accept, in the order they are tried
     * @return one strict formatter per pattern, in the same order
     */
    private static List<DateTimeFormatter> strictFormats(String... patterns) {
        return Arrays.stream(patterns)
                .map(pattern -> DateTimeFormatter.ofPattern(pattern, FORMAT_LOCALE)
                        .withResolverStyle(ResolverStyle.STRICT))
                .toList();
    }

    /**
     * Reads a date, and optionally a time, from text the user typed or that was
     * read back from the save file.
     *
     * <p>Each accepted format is tried in turn; a format that does not match
     * throws, which simply means the next one is tried.
     *
     * @param text the date text to interpret
     * @return the date it describes
     * @throws BibiException if the text matches none of the accepted formats
     */
    public static TaskDateTime parse(String text) throws BibiException {
        if (text == null || text.isBlank()) {
            throw new BibiException("A date is missing. " + FORMAT_HELP);
        }
        String trimmedText = text.trim();

        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                LocalDateTime parsed = LocalDateTime.parse(trimmedText, format);
                return new TaskDateTime(parsed.toLocalDate(), parsed.toLocalTime());
            } catch (DateTimeParseException notThisFormat) {
                continue;
            }
        }
        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return new TaskDateTime(LocalDate.parse(trimmedText, format), null);
            } catch (DateTimeParseException notThisFormat) {
                continue;
            }
        }
        throw new BibiException("I could not read the date '" + trimmedText + "'. " + FORMAT_HELP);
    }

    /**
     * Returns a plain date in the same display form used by tasks.
     *
     * @param date the date to format
     * @return the date as {@code Oct 15 2019}
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Returns the calendar date, ignoring any time of day.
     *
     * @return the date part of this value
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Reports whether this value falls on the supplied calendar date.
     *
     * @param queryDate the date being asked about
     * @return {@code true} when the dates match
     */
    public boolean isOn(LocalDate queryDate) {
        return date.equals(queryDate);
    }

    /**
     * Reports whether this value comes before another one.
     *
     * <p>A whole-day value counts as the start of its day, so a date without a
     * time never sorts after a time on the same day.
     *
     * @param other the value to compare against
     * @return {@code true} when this value is the earlier of the two
     */
    public boolean isBefore(TaskDateTime other) {
        return toComparable().isBefore(other.toComparable());
    }

    /**
     * Returns this value as a single instant so two values can be compared.
     *
     * @return the date combined with its time, or with midnight when it has none
     */
    private LocalDateTime toComparable() {
        return LocalDateTime.of(date, time == null ? LocalTime.MIDNIGHT : time);
    }

    /**
     * Returns this value in the form used inside the save file.
     *
     * <p>The saved form is also an accepted input format, which keeps the save
     * file readable and editable by hand.
     *
     * @return {@code 2019-12-02} or {@code 2019-12-02 1800}
     */
    public String toStorageString() {
        String storedDate = date.toString();
        return time == null ? storedDate : storedDate + " " + timeAsFourDigits();
    }

    /**
     * Returns the time of day as the four digits used by the save file.
     *
     * @return the time in {@code HHmm} form
     */
    private String timeAsFourDigits() {
        return String.format("%02d%02d", time.getHour(), time.getMinute());
    }

    /**
     * Returns the form shown to the user.
     *
     * @return {@code Oct 15 2019}, or {@code Dec 02 2019 6:00PM} when a time is set
     */
    @Override
    public String toString() {
        String shownDate = date.format(DISPLAY_DATE);
        return time == null ? shownDate : shownDate + " " + time.format(DISPLAY_TIME);
    }
}
