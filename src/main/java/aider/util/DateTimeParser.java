package aider.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import aider.AiderException;

/** Parses and formats the date and time values used by Aider tasks. */
public final class DateTimeParser {
    /** Format used when displaying date-only values to users. */
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Format used when displaying date-time values to users. */
    private static final DateTimeFormatter DATE_TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

    /** Format used when storing date and time values on disk. */
    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses a date or date-time. Accepted forms are yyyy-MM-dd, yyyy-MM-dd HHmm,
     * yyyy-MM-dd HH:mm, and d/M/yyyy HHmm.
     *
     * @param value the date or date-time text
     * @return the parsed date and time
     * @throws AiderException if the value is not valid
     */
    public static LocalDateTime parse(String value) throws AiderException {
        if (value == null || value.trim().isEmpty()) {
            throw new AiderException("A date or time must be provided.");
        }
        String text = value.trim();
        DateTimeFormatter[] formats = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm")
        };

        for (DateTimeFormatter format : formats) {
            try {
                return LocalDateTime.parse(text, format);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }

        try {
            return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException exception) {
            throw new AiderException("Invalid date or time: " + value
                    + ". Use yyyy-MM-dd or yyyy-MM-dd HHmm.");
        }
    }

    /**
     * Formats a date-time for display.
     *
     * @param dateTime the date-time to format
     * @return the user-facing date-time text
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DATE_DISPLAY_FORMAT);
        }
        return dateTime.format(DATE_TIME_DISPLAY_FORMAT);
    }

    /**
     * Formats a date-time for storage.
     *
     * @param dateTime the date-time to format
     * @return the storage representation
     */
    public static String toStorageString(LocalDateTime dateTime) {
        return dateTime.format(STORAGE_FORMAT);
    }
}
