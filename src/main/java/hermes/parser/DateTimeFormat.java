package hermes.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import hermes.HermesException;

/**
 * List of the various DateFormat and DateTimeFormat Hermes can accept.
 *
 * <p>This names the type of Date and DateTimeFormat users might input and converts
 * the String into a DateTimeFormatter.
 *
 * <p>A boolean variable called isDateOnly to differentiate between DateFormat and DateTimeFormat.
 */
public enum DateTimeFormat {

    ISO_DATE("uuuu-MM-dd", true),
    SPACED_ISO_DATE("uuuu MM dd", true),
    SLASHED_DATE("d/M/uuuu", true),
    NAMED_MONTH_DATE("d MMM uuuu", true),
    MONTH_FIRST_DATE("MMM d uuuu", true),
    ISO_DATE_TIME("uuuu-MM-dd HHmm", false),
    ISO_DATE_TIME_WITH_COLON("uuuu-MM-dd HH:mm", false),
    SLASHED_ISO_DATE_TIME("uuuu/MM/dd HHmm", false),
    SLASHED_ISO_DATE_TIME_WITH_COLON("uuuu/MM/dd HH:mm", false),
    SLASHED_DATE_TIME("d/M/uuuu HHmm", false),
    SLASHED_DATE_TIME_WITH_COLON("d/M/uuuu HH:mm", false),
    SLASHED_DATE_TIME_12_HOUR("d/M/uuuu h:mma", false),
    DASHED_DATE_TIME("d-M-uuuu HHmm", false),
    DASHED_DATE_TIME_WITH_COLON("d-M-uuuu HH:mm", false),
    NAMED_MONTH_DATE_TIME("d MMM uuuu HHmm", false),
    NAMED_MONTH_DATE_TIME_WITH_COLON("d MMM uuuu HH:mm", false),
    NAMED_MONTH_DATE_TIME_12_HOUR("d MMM uuuu h:mma", false),
    MONTH_FIRST_DATE_TIME("MMM d uuuu HHmm", false),
    MONTH_FIRST_DATE_TIME_WITH_COLON("MMM d uuuu HH:mm", false);

    private final DateTimeFormatter formatter;
    private final boolean isDateOnly;

    /**
     * Converts the date (time) {@link String} into a {@link DateTimeFormatter}
     * and stores a boolean value isDateOnly to determine what kind of method
     * to use to parse a potential date (time) string.
     *
     * @param format date (time) format as a String.
     * @param isDateOnly whether format string contains any time value.
     */
    DateTimeFormat(String format, boolean isDateOnly) {
        // Resolving strictly refuses a date that does not exist, such as 30
        // February. The default would quietly adjust it to the nearest real
        // date instead, storing a day the user never typed. Strict resolving
        // needs uuuu rather than yyyy: yyyy is the year within an era, which
        // strict mode will not accept without being told the era as well.
        this.formatter = DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT);
        this.isDateOnly = isDateOnly;
    }

    /**
     * Analyses a Date Time String and attempts to parse the value with an appropriate formatter.
     *
     * @param dateTime String of the date (and time) inputted by the users.
     * @return the appropriate DateTimeFormatter if present else returns null.
     */
    private static DateTimeFormat findMatchingFormat(String dateTime) {
        for (DateTimeFormat format : DateTimeFormat.values()) {
            if (matchesFormat(dateTime, format.formatter, format.isDateOnly)) {
                return format;
            }
        }
        return null;
    }

    /**
     * Returns true if the formatter can read the date (and time) the user typed.
     *
     * @param dateTime String of the date (and time) the user inputted.
     * @param formatter A DateTimeFormatter that will be used to attempt to parse the dateTime.
     * @param isDateOnly Determines if LocalDate.parse or LocalDateTime.parse is used.
     * @return true if format is able to parse the dateTime.
     */
    private static boolean matchesFormat(String dateTime, DateTimeFormatter formatter, boolean isDateOnly) {
        try {
            if (isDateOnly) {
                LocalDate.parse(dateTime, formatter);
            } else {
                LocalDateTime.parse(dateTime, formatter);
            }
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    /**
     * Parses the date (and time) the user typed and converts it into a LocalDateTime.
     *
     * @param dateTime Date (and time) the user typed.
     * @return The input parsed as a LocalDateTime.
     * @throws HermesException If the input is not in a recognised format.
     */
    public static LocalDateTime parseDateTime(String dateTime) throws HermesException {
        DateTimeFormat format = DateTimeFormat.findMatchingFormat(dateTime);

        if (format == null) {
            throw new HermesException("'" + dateTime
                    + "' is no date I can read. Write it thus, for instance: 17/08/2026 1500");
        }

        DateTimeFormatter formatter = format.getFormatter();

        return format.isDateOnly
                ? LocalDate.parse(dateTime, formatter).atStartOfDay()
                : LocalDateTime.parse(dateTime, formatter);
    }
}
