package fein;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Converts Fein's numeric date-and-time input into a typed date and time. */
public final class DateTimeParser {
    private static final String NUMERIC_DATE_TIME_PATTERN = "\\d{1,2}/\\d{1,2}/(?:\\d{2}|\\d{4}) \\d{4}";
    private static final String NUMERIC_DATE_PATTERN = "\\d{1,2}/\\d{1,2}/(?:\\d{2}|\\d{4})";
    private static final String DISPLAY_DATE_TIME_PATTERN = "[A-Za-z]{3} \\d{1,2} \\d{4}, \\d{1,2}:\\d{2} [AP]M";

    /** The input format: day/month/year followed by a 24-hour time without a colon. */
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    /** The input format for a numeric date without a time. */
    private static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    /** The readable format shown to the user for parsed date and time values. */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter
            .ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);

    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses supported numeric input, returning {@code null} for natural-language deadlines.
     *
     * @param value the date and optional time to parse
     * @return the parsed date and time, or {@code null} when the value is unsupported or invalid
     */
    public static LocalDateTime parse(String value) {
        // Callers only pass a deadline's non-blank text to the date/time parser.
        assert value != null : "A deadline value must be present before parsing";

        if (value.matches(NUMERIC_DATE_TIME_PATTERN)) {
            try {
                return LocalDateTime.parse(expandTwoDigitYear(value), INPUT_FORMAT);
            } catch (DateTimeParseException exception) {
                return null;
            }
        }
        if (value.matches(DISPLAY_DATE_TIME_PATTERN)) {
            try {
                return LocalDateTime.parse(value, DISPLAY_FORMAT);
            } catch (DateTimeParseException exception) {
                return null;
            }
        }
        return null;
    }

    /** Expands a two-digit numeric year into a year from 2000 to 2099. */
    private static String expandTwoDigitYear(String value) {
        int timeSeparator = value.indexOf(' ');
        int dateEnd = timeSeparator >= 0 ? timeSeparator : value.length();
        int finalDateSeparator = value.lastIndexOf('/', dateEnd);
        String year = value.substring(finalDateSeparator + 1, dateEnd);
        if (year.length() == 2) {
            return value.substring(0, finalDateSeparator + 1) + "20" + value.substring(finalDateSeparator + 1);
        }
        return value;
    }

    /**
     * Returns whether text resembles a numeric or displayed date-and-time value.
     *
     * @param value the date and optional time to check
     * @return whether the value has a supported date or date-and-time shape
     */
    public static boolean resemblesDateTime(String value) {
        // Callers validate a non-null command field before asking whether it resembles a date and time.
        assert value != null : "A date-and-time value must be present before checking its format";
        return value.matches(NUMERIC_DATE_PATTERN + "(?: \\d{4})?")
                || value.matches(DISPLAY_DATE_TIME_PATTERN);
    }

    /**
     * Returns whether a numeric or displayed date value represents a real calendar date.
     *
     * @param value the date and optional time to validate
     * @return whether the value represents a valid supported date or date and time
     */
    public static boolean isValidDateTime(String value) {
        // Callers first check that the value resembles a supported date before validating it.
        assert value != null : "A date-and-time value must be present before validating it";
        if (value.matches(NUMERIC_DATE_PATTERN)) {
            try {
                LocalDate.parse(expandTwoDigitYear(value), DATE_INPUT_FORMAT);
                return true;
            } catch (DateTimeParseException exception) {
                return false;
            }
        }
        return parse(value) != null;
    }

    /**
     * Formats a typed date and time for display and persistence.
     *
     * @param value the date and time to format
     * @return the human-readable date and time
     */
    public static String format(LocalDateTime value) {
        // Formatting is only meaningful after a supported date/time has been parsed.
        assert value != null : "A parsed date/time value must be present before formatting";
        return value.format(DISPLAY_FORMAT);
    }
}
