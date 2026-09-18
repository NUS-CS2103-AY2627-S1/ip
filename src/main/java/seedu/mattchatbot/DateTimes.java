package seedu.mattchatbot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Reads dates and times the user types, and formats them for display and for
 * the save file.
 * <p>
 * Kept in one class so that the accepted input formats and the displayed
 * format are defined next to each other, rather than being spread across the
 * task types that use them.
 */
public class DateTimes {

    /**
     * Date on its own, as typed by the user, e.g. {@code 2019-10-15}.
     * <p>
     * Resolved strictly, so that an impossible date such as 2019-02-30 is
     * rejected rather than quietly moved to the last day of the month, which
     * is what the default resolver does. Strict resolving needs the proleptic
     * year "uuuu" rather than the year-of-era "yyyy", which would require an
     * era to be given as well.
     */
    private static final DateTimeFormatter INPUT_DATE =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    /** Date with a time, as typed by the user, e.g. {@code 2019-10-15 1800}. */
    private static final DateTimeFormatter INPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);

    /** Date as shown back to the user, e.g. {@code Oct 15 2019}. */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    /** Date and time as shown back to the user, e.g. {@code Oct 15 2019, 6:00pm}. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    /**
     * The single format used in the save file.
     * <p>
     * Always written with a time, so that reading a line back never has to
     * guess which of the two input formats was originally used.
     */
    private static final DateTimeFormatter FILE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /** Not meant to be instantiated: every member of DateTimes is static. */
    private DateTimes() {
    }

    /**
     * Reads a date, with an optional time, as typed by the user.
     *
     * @param input the text the user typed after /by, /from or /to
     * @return the moment it names, at midnight if no time was given
     * @throws MattChatBotException if the text is not a date this understands
     */
    public static LocalDateTime parse(String input) throws MattChatBotException {
        String trimmed = input.trim();
        try {
            return LocalDateTime.parse(trimmed, INPUT_DATE_TIME);
        } catch (DateTimeParseException withoutTime) {
            // Not a date and time, so try a bare date before giving up.
            try {
                return LocalDate.parse(trimmed, INPUT_DATE).atStartOfDay();
            } catch (DateTimeParseException notADate) {
                throw new MattChatBotException("I could not make sense of the date \""
                        + trimmed + "\". It should be written as yyyy-MM-dd, e.g. 2019-10-15,"
                        + " with a time if you wish, e.g. 2019-10-15 1800.");
            }
        }
    }

    /**
     * Reads a plain date, ignoring any time of day.
     *
     * @param input the text the user typed
     * @return the date it names
     * @throws MattChatBotException if the text is not a date this understands
     */
    public static LocalDate parseDate(String input) throws MattChatBotException {
        return parse(input).toLocalDate();
    }

    /**
     * Formats a moment for display, showing the time only when there is one.
     * <p>
     * A task saved without a time is stored at midnight, so midnight is shown
     * as a bare date. The cost is that a task genuinely due at 00:00 also
     * displays as a bare date, which is a small price for not having to carry
     * a separate "has a time" flag on every task.
     *
     * @param when the moment to format
     * @return the text to show the user
     */
    public static String format(LocalDateTime when) {
        if (when.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return when.format(DISPLAY_DATE);
        }
        return when.format(DISPLAY_DATE_TIME).replace("AM", "am").replace("PM", "pm");
    }

    /**
     * Formats a plain date for display, e.g. {@code Oct 15 2019}.
     *
     * @param date the date to format
     * @return the text to show the user
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Formats a moment for the save file.
     *
     * @param when the moment to format
     * @return the text to write to the save file
     */
    public static String toFileString(LocalDateTime when) {
        return when.format(FILE_FORMAT);
    }
}
