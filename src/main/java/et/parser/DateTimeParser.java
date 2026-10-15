package et.parser;

import et.exception.ETException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Parses, formats, and serialises the date values accepted by ET commands.
 */
public final class DateTimeParser {
    /** The message shown when a command date cannot be parsed. */
    private static final String INVALID_DATE_MESSAGE = "Please enter a valid date, optionally followed by a time. "
            + "Examples: 2019-01-05, 5/1/2019, 5 Jan 2019, or Jan 5, 2019 6:30 PM.";

    /**
     * The accepted command date formats.
     *
     * <p>Day-first numeric formats precede month-first formats so ambiguous dates retain ET's
     * existing day-first interpretation.</p>
     */
    private static final List<DateTimeFormatter> INPUT_DATE_FORMATS = List.of(
            createInputFormatter("uuuu-M-d"),
            createInputFormatter("uuuu/M/d"),
            createInputFormatter("uuuu.M.d"),
            createInputFormatter("d/M/uuuu"),
            createInputFormatter("d-M-uuuu"),
            createInputFormatter("d.M.uuuu"),
            createInputFormatter("M/d/uuuu"),
            createInputFormatter("M-d-uuuu"),
            createInputFormatter("M.d.uuuu"),
            createInputFormatter("d MMM uuuu"),
            createInputFormatter("d MMMM uuuu"),
            createInputFormatter("MMM d uuuu"),
            createInputFormatter("MMMM d uuuu"),
            createInputFormatter("d-MMM-uuuu"),
            createInputFormatter("d-MMMM-uuuu"),
            createInputFormatter("MMM-d-uuuu"),
            createInputFormatter("MMMM-d-uuuu"));

    /** The accepted command time formats, which always follow a date. */
    private static final List<DateTimeFormatter> INPUT_TIME_FORMATS = List.of(
            createInputFormatter("HHmm"),
            createInputFormatter("Hmm"),
            createInputFormatter("H:mm"),
            createInputFormatter("H.mm"),
            createInputFormatter("h:mm a"),
            createInputFormatter("h:mma"),
            createInputFormatter("h.mm a"),
            createInputFormatter("h.mma"),
            createInputFormatter("h a"),
            createInputFormatter("ha"));

    /** The format used when showing a date without a time. */
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /** The format used when showing a date and time. */
    private static final DateTimeFormatter OUTPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH);

    /** Prevents instantiation of this utility class. */
    private DateTimeParser() {
    }

    /**
     * Creates a strict, case-insensitive formatter for a supported input pattern.
     *
     * @param pattern the date or time pattern to accept
     * @return the configured input formatter
     */
    private static DateTimeFormatter createInputFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Parses a command date in one of ET's supported formats.
     *
     * @param input the date text provided after a command marker
     * @return the parsed value and whether the input included a time
     * @throws ETException if the input is not a valid supported date
     */
    public static ParsedDateTime parse(String input) throws ETException {
        String normalizedInput = normalizeInput(input);
        Optional<LocalDate> date = parseDate(normalizedInput);
        if (date.isPresent()) {
            return new ParsedDateTime(date.get().atStartOfDay(), false);
        }

        int splitPosition = normalizedInput.lastIndexOf(' ');
        while (splitPosition > 0) {
            String dateInput = normalizedInput.substring(0, splitPosition);
            String timeInput = normalizedInput.substring(splitPosition + 1);
            date = parseDate(dateInput);
            Optional<LocalTime> time = parseTime(timeInput);
            if (date.isPresent() && time.isPresent()) {
                return new ParsedDateTime(LocalDateTime.of(date.get(), time.get()), true);
            }
            splitPosition = normalizedInput.lastIndexOf(' ', splitPosition - 1);
        }

        throw new ETException(INVALID_DATE_MESSAGE);
    }

    /**
     * Normalizes harmless punctuation and spacing differences in command dates.
     *
     * @param input the raw date and optional time input
     * @return the normalized input used by the supported formatters
     */
    private static String normalizeInput(String input) {
        assert input != null : "Date input must be provided";
        return input.strip().replace(",", "").replaceAll("\\s+", " ");
    }

    /**
     * Parses a date using each supported input format.
     *
     * @param input the normalized date text
     * @return the parsed date, or an empty value when no format matches
     */
    private static Optional<LocalDate> parseDate(String input) {
        for (DateTimeFormatter dateFormatter : INPUT_DATE_FORMATS) {
            try {
                return Optional.of(LocalDate.parse(input, dateFormatter));
            } catch (DateTimeParseException ignored) {
                // Continue to the next supported date format.
            }
        }
        return Optional.empty();
    }

    /**
     * Parses a time using each supported input format.
     *
     * @param input the normalized time text
     * @return the parsed time, or an empty value when no format matches
     */
    private static Optional<LocalTime> parseTime(String input) {
        for (DateTimeFormatter timeFormatter : INPUT_TIME_FORMATS) {
            try {
                return Optional.of(LocalTime.parse(input, timeFormatter));
            } catch (DateTimeParseException ignored) {
                // Continue to the next supported time format.
            }
        }
        return Optional.empty();
    }

    /**
     * Recreates a saved date value, whose presence of {@code T} identifies a time.
     *
     * @param storedValue the ISO date or ISO date-time saved by ET
     * @return the reconstructed value and whether it includes a time
     * @throws IllegalArgumentException if the saved value is malformed
     */
    public static ParsedDateTime parseStored(String storedValue) {
        try {
            if (storedValue.contains("T")) {
                return new ParsedDateTime(LocalDateTime.parse(storedValue), true);
            }
            return new ParsedDateTime(LocalDate.parse(storedValue).atStartOfDay(), false);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid saved date", e);
        }
    }

    /**
     * Formats a date value for task-list output.
     *
     * @param dateTime the date value to display
     * @param hasTime whether the user supplied a time
     * @return a human-readable date or date and time
     */
    public static String format(LocalDateTime dateTime, boolean hasTime) {
        assert dateTime != null : "Date value to format must be provided";
        return hasTime ? dateTime.format(OUTPUT_DATE_TIME_FORMAT) : dateTime.format(OUTPUT_DATE_FORMAT);
    }

    /**
     * Converts a date value into the stable ISO text used in storage.
     *
     * @param dateTime the date value to save
     * @param hasTime whether the value includes a time
     * @return the ISO date or ISO date-time value
     */
    public static String formatForStorage(LocalDateTime dateTime, boolean hasTime) {
        assert dateTime != null : "Date value to store must be provided";
        return hasTime ? dateTime.toString() : dateTime.toLocalDate().toString();
    }

    /**
     * Holds a parsed date value together with whether its input included a time.
     *
     * @param value the parsed date and time; date-only values use midnight
     * @param hasTime whether the original input included a time
     */
    public record ParsedDateTime(LocalDateTime value, boolean hasTime) {
    }
}
