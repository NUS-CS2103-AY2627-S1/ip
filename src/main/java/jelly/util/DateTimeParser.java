package jelly.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/** Converts user-entered date/time text into date-times. */
public class DateTimeParser {
    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter STORAGE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses the format entered by a user.
     *
     * @param date the date-time text to parse
     * @return the parsed date-time
     */
    public static LocalDateTime parse(String date) {
        return LocalDateTime.parse(date, INPUT_FORMATTER);
    }

    /** Parses the ISO date-time format used internally by the save file. */
    public static LocalDateTime parseStored(String date) {
        return LocalDateTime.parse(date, STORAGE_FORMATTER);
    }
}
