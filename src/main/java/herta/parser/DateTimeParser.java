package herta.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Parses and formats the date/time values used by deadline and event tasks.
 *
 * <p>User input may use either the slash format from the command example or
 * the ISO-like format from the minimal requirement. Stored values always use
 * Java's ISO local date/time format so that they can be parsed reliably when
 * Herta starts again. Herta accepts dates from {@code 0001-01-01} through
 * {@code 9999-12-31}; past dates remain valid historical task data.</p>
 */
public final class DateTimeParser {
    private static final LocalDate MINIMUM_SUPPORTED_DATE = LocalDate.of(1, 1, 1);
    private static final LocalDate MAXIMUM_SUPPORTED_DATE = LocalDate.of(9_999, 12, 31);

    private static final List<DateTimeFormatter> USER_DATE_TIME_FORMATS = List.of(
            createStrictFormatter("d/M/uuuu HHmm"),
            createStrictFormatter("uuuu-MM-dd HHmm"),
            createStrictFormatter("uuuu-MM-dd HH:mm"));

    private static final DateTimeFormatter ISO_DATE_FORMAT =
            createStrictFormatter("uuuu-MM-dd");
    private static final DateTimeFormatter SLASH_DATE_FORMAT = createStrictFormatter("d/M/uuuu");
    private static final DateTimeFormatter STORAGE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DATE_OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);

    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses a date/time entered by the user for a deadline or event.
     *
     * @param input the date/time entered after a task date marker.
     * @return the parsed date/time, with date-only input represented at midnight.
     * @throws DateTimeParseException if the input does not match a supported format.
     */
    public static LocalDateTime parseUserDateTime(String input) {
        String normalizedInput = normalizeInput(input);

        for (DateTimeFormatter formatter : USER_DATE_TIME_FORMATS) {
            try {
                return validateSupportedDateTime(LocalDateTime.parse(normalizedInput, formatter),
                        normalizedInput);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date/time format.
            }
        }

        return parseUserDate(normalizedInput).atStartOfDay();
    }

    /**
     * Parses a date used by a date-filter command.
     *
     * @param input the date entered by the user.
     * @return the parsed date.
     * @throws DateTimeParseException if the input does not match a supported date format.
     */
    public static LocalDate parseUserDate(String input) {
        String normalizedInput = normalizeInput(input);

        try {
            return validateSupportedDate(LocalDate.parse(normalizedInput, ISO_DATE_FORMAT),
                    normalizedInput);
        } catch (DateTimeParseException e) {
            return validateSupportedDate(LocalDate.parse(normalizedInput, SLASH_DATE_FORMAT),
                    normalizedInput);
        }
    }

    /**
     * Parses a date/time serialized in Herta's data file.
     *
     * @param input the serialized date/time.
     * @return the parsed date/time.
     * @throws DateTimeParseException if the stored value is invalid.
     */
    public static LocalDateTime parseStoredDateTime(String input) {
        String normalizedInput = normalizeInput(input);
        try {
            return validateSupportedDateTime(LocalDateTime.parse(normalizedInput, STORAGE_FORMAT),
                    normalizedInput);
        } catch (DateTimeParseException e) {
            LocalDate date = LocalDate.parse(normalizedInput, ISO_DATE_FORMAT);
            return validateSupportedDateTime(date.atStartOfDay(), normalizedInput);
        }
    }

    /**
     * Validates a domain date/time against Herta's documented business range.
     * Dates before year 1 or after year 9999 are rejected to keep date arithmetic safe.
     *
     * @param dateTime the date/time to validate.
     * @throws IllegalArgumentException if the date is outside the supported range.
     */
    public static void validateSupportedDateTime(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "A date/time cannot be null.");
        if (isOutsideSupportedRange(dateTime.toLocalDate())) {
            throw new IllegalArgumentException("Dates must be between 0001-01-01 and 9999-12-31.");
        }
    }

    /** Creates a parse failure for a syntactically valid but unsupported date. */
    private static LocalDateTime validateSupportedDateTime(LocalDateTime dateTime, String input) {
        if (isOutsideSupportedRange(dateTime.toLocalDate())) {
            throw new DateTimeParseException("Date is outside Herta's supported range.", input, 0);
        }
        return dateTime;
    }

    /** Creates a parse failure for a syntactically valid but unsupported date. */
    private static LocalDate validateSupportedDate(LocalDate date, String input) {
        if (isOutsideSupportedRange(date)) {
            throw new DateTimeParseException("Date is outside Herta's supported range.", input, 0);
        }
        return date;
    }

    /** Indicates whether a date lies outside the documented business range. */
    private static boolean isOutsideSupportedRange(LocalDate date) {
        return date.isBefore(MINIMUM_SUPPORTED_DATE) || date.isAfter(MAXIMUM_SUPPORTED_DATE);
    }

    /**
     * Formats a date/time for storage in Herta's data file.
     *
     * @param dateTime the date/time to serialize.
     * @return the stable serialized representation.
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return STORAGE_FORMAT.format(Objects.requireNonNull(dateTime));
    }

    /**
     * Formats a date/time for display to the user.
     *
     * @param dateTime the date/time to display.
     * @return a readable date or date/time representation.
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "A date/time cannot be null.");
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return DATE_OUTPUT_FORMAT.format(dateTime);
        }
        return DATE_TIME_OUTPUT_FORMAT.format(dateTime);
    }

    /**
     * Formats a calendar date for display to the user.
     *
     * @param date the date to display.
     * @return a readable date representation.
     */
    public static String formatDateForDisplay(LocalDate date) {
        return DATE_OUTPUT_FORMAT.format(Objects.requireNonNull(date, "A date cannot be null."));
    }

    /**
     * Creates a strict formatter using a pattern with a proleptic year.
     *
     * @param pattern the date/time pattern.
     * @return a strict formatter.
     */
    private static DateTimeFormatter createStrictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /** Trims the input and treats repeated horizontal whitespace as one separator. */
    private static String normalizeInput(String input) {
        return Objects.requireNonNull(input, "A date input cannot be null.")
                .trim()
                .replaceAll("[ \\t]+", " ");
    }
}
