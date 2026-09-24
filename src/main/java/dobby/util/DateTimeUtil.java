package dobby.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import dobby.exception.DobbyException;

/**
 * Parses, formats, and saves the dates and times used by dated tasks.
 */
public final class DateTimeUtil {
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter INPUT_SLASH_DATE = DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_TIME = DateTimeFormatter.ofPattern("HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_TIME_WITH_COLON = DateTimeFormatter.ofPattern("HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd uuuu, HH:mm");
    private static final DateTimeFormatter SAVED_DATE_TIME = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");

    private DateTimeUtil() {
    }

    /**
     * Parses a date or a date followed by a 24-hour time.
     *
     * @param input date text in {@code yyyy-MM-dd} or {@code d/M/yyyy} format, optionally followed by a time
     * @return the parsed value and whether the user supplied a time
     * @throws DobbyException if the input is not a valid supported date or time
     */
    public static ParsedDateTime parse(String input) throws DobbyException {
        assert input != null : "Date input must originate from a parsed command or saved record";

        String[] parts = input.trim().split("\\s+");
        if (parts.length < 1 || parts.length > 2) {
            throw new DobbyException("Dobby needs a date with an optional time.");
        }

        try {
            LocalDate date = parseDate(parts[0]);
            if (parts.length == 1) {
                return new ParsedDateTime(date.atStartOfDay(), false);
            }

            LocalTime time = parseTime(parts[1]);
            return new ParsedDateTime(LocalDateTime.of(date, time), true);
        } catch (DateTimeParseException e) {
            throw new DobbyException("Dobby needs a valid date or time.", e);
        }
    }

    /** Formats a date/time for Dobby's task list. */
    public static String formatForDisplay(LocalDateTime dateTime, boolean hasTime) {
        return hasTime ? dateTime.format(DISPLAY_DATE_TIME) : dateTime.format(DISPLAY_DATE);
    }

    /** Formats a date/time for Dobby's data file. */
    public static String formatForStorage(LocalDateTime dateTime, boolean hasTime) {
        return hasTime ? dateTime.format(SAVED_DATE_TIME) : dateTime.toLocalDate().format(INPUT_DATE);
    }

    /**
     * Parses a time in compact or colon-separated 24-hour format.
     *
     * @param text time text to parse
     * @return the parsed time
     * @throws DateTimeParseException if the text is not in a supported time format
     */
    private static LocalTime parseTime(String text) {
        try {
            return LocalTime.parse(text, INPUT_TIME);
        } catch (DateTimeParseException e) {
            return LocalTime.parse(text, INPUT_TIME_WITH_COLON);
        }
    }

    /**
     * Parses a date in ISO or slash-separated format.
     *
     * @param text date text to parse
     * @return the parsed date
     * @throws DateTimeParseException if the text is not in a supported date format
     */
    private static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text, INPUT_DATE);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(text, INPUT_SLASH_DATE);
        }
    }

    /** A parsed date/time together with whether it included a time component. */
    public static final class ParsedDateTime {
        private final LocalDateTime value;
        private final boolean hasTime;

        private ParsedDateTime(LocalDateTime value, boolean hasTime) {
            assert value != null : "A parsed date/time must have a value";
            assert hasTime || value.toLocalTime().equals(LocalTime.MIDNIGHT)
                    : "A date without an explicit time must use midnight";

            this.value = value;
            this.hasTime = hasTime;
        }

        /** Returns the parsed date and time. */
        public LocalDateTime getValue() {
            return value;
        }

        /** Returns whether the original input explicitly supplied a time. */
        public boolean hasTime() {
            return hasTime;
        }
    }
}
