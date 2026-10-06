package com.gihun456.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses user-provided date-time values and handles the date-time format used by storage.
 */
public final class DateTimeParser {
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
        DateTimeFormatter.ofPattern("dd/MM/yyyy HHmm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm")
    };
    private static final DateTimeFormatter[] DATE_ONLY_FORMATTERS = {
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };
    private static final DateTimeFormatter STORAGE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateTimeParser() {
    }

    /**
     * Parses a user-provided date with an optional HHmm time.
     *
     * @param dateTimeText Date-time text entered by the user.
     * @return Parsed date-time.
     * @throws DateTimeParseException If the text does not use a supported format.
     */
    public static LocalDateTime parseUserDateTime(String dateTimeText) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateTimeText, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date-time format.
            }
        }

        for (DateTimeFormatter formatter : DATE_ONLY_FORMATTERS) {
            try {
                return LocalDate.parse(dateTimeText, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next supported date-only format.
            }
        }

        throw new DateTimeParseException("Unsupported date-time format", dateTimeText, 0);
    }

    /**
     * Formats a date-time for storage.
     *
     * @param dateTime Date-time to format.
     * @return ISO date-time string.
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return STORAGE_FORMATTER.format(dateTime);
    }

    /**
     * Parses a date-time read from storage.
     *
     * @param dateTimeText Stored ISO date-time text.
     * @return Parsed date-time.
     */
    public static LocalDateTime parseStoredDateTime(String dateTimeText) {
        return LocalDateTime.parse(dateTimeText, STORAGE_FORMATTER);
    }
}
