package lumine.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import lumine.LumineException;

/**
 * Represents a task date or time that may remain as free-form text.
 * Structured date values are formatted consistently for storage and display.
 */
final class TaskDateTime {
    private static final String DATE_REGEX = "\\d{4} \\d{2} \\d{2}";
    private static final String DATE_TIME_REGEX = DATE_REGEX + " \\d{4}";
    private static final String DATE_PREFIX_REGEX = DATE_REGEX + "(?: .*)?";
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("uuuu MM dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu MM dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);

    private final String rawText;
    private final LocalDate date;
    private final LocalDateTime dateTime;

    /**
     * Creates a task date-time value, parsing recognized structured formats.
     * Invalid structured values are rejected; unrecognized values remain free-form text.
     *
     * @param rawText Validated non-blank task date-time text.
     */
    TaskDateTime(String rawText) {
        this.rawText = rawText;

        LocalDate parsedDate = null;
        LocalDateTime parsedDateTime = null;
        String normalizedText = rawText.replaceAll("\\s+", " ");
        try {
            if (normalizedText.matches(DATE_REGEX)) {
                parsedDate = LocalDate.parse(normalizedText, INPUT_DATE_FORMAT);
            } else if (hasDateTimeFormat(normalizedText)) {
                parsedDateTime = LocalDateTime.parse(normalizedText, INPUT_DATE_TIME_FORMAT);
            }
        } catch (DateTimeParseException exception) {
            throw new LumineException("Date not found :<.\nPlease enter a valid calendar date or time.");
        }
        this.date = parsedDate;
        this.dateTime = parsedDateTime;
    }

    /**
     * Returns whether normalized text starts with a structured date followed by a space or its end.
     * This checks the text's shape, not whether the calendar date exists.
     */
    static boolean hasDatePrefix(String normalizedText) {
        return normalizedText.matches(DATE_PREFIX_REGEX);
    }

    /**
     * Returns whether normalized text has the structured date format and a four-digit time.
     * Calendar validity is checked separately when the value is parsed.
     */
    static boolean hasDateTimeFormat(String normalizedText) {
        return normalizedText.matches(DATE_TIME_REGEX);
    }

    /**
     * Returns the canonical structured value or the original free-form text for storage.
     */
    String formatForStorage() {
        if (dateTime != null) {
            return dateTime.format(INPUT_DATE_TIME_FORMAT);
        }
        if (date != null) {
            return date.format(INPUT_DATE_FORMAT);
        }
        return rawText;
    }

    /**
     * Returns a human-readable structured value or the original free-form text.
     */
    String formatForDisplay() {
        if (dateTime != null) {
            return dateTime.format(DISPLAY_DATE_TIME_FORMAT);
        }
        if (date != null) {
            return date.format(DISPLAY_DATE_FORMAT);
        }
        return rawText;
    }

    /**
     * Returns the represented calendar date, or null when the value is free-form text.
     */
    LocalDate toLocalDate() {
        if (dateTime != null) {
            return dateTime.toLocalDate();
        }
        return date;
    }

    /**
     * Returns the represented date-time, or null for date-only and free-form values.
     */
    LocalDateTime toLocalDateTime() {
        return dateTime;
    }
}
