package ramly.validation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import ramly.exception.TaskValidationException;

/** Parses supported date-time formats using strict calendar validation. */
public final class DateTimeParser {
    private static final DateTimeFormatter STORAGE_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_DATE_TIME = strictFormatter("uuuu-MM-dd HHmm");
    private static final DateTimeFormatter SHORT_DATE_TIME = strictFormatter("d/M/uuuu HHmm");
    private static final List<DateTimeFormatter> EVENT_FORMATS = List.of(
            STORAGE_DATE_TIME, ISO_DATE_TIME, SHORT_DATE_TIME);

    private DateTimeParser() {
    }

    /** Parses a deadline date with an optional time. */
    public static LocalDateTime parseDeadlineInput(String value) {
        String normalized = normalizeDateTime(value, "deadline");
        try {
            return LocalDate.parse(normalized, ISO_DATE).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // Continue with date-time formats.
        }
        Optional<LocalDateTime> parsed = parseUsing(normalized, List.of(ISO_DATE_TIME, SHORT_DATE_TIME));
        if (parsed.isPresent()) {
            return parsed.get();
        }
        throw new TaskValidationException(
                "That date does not exist. Use yyyy-MM-dd, yyyy-MM-dd HHmm, or d/M/yyyy HHmm.");
    }

    /** Parses the canonical storage format or a legacy user-facing deadline format. */
    public static LocalDateTime parseStoredDeadline(String value) {
        String normalized = normalizeDateTime(value, "deadline");
        try {
            return LocalDateTime.parse(normalized, STORAGE_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            return parseDeadlineInput(normalized);
        }
    }

    /** Parses a required event boundary. */
    public static LocalDateTime parseEventBoundary(String value) {
        String normalized = normalizeDateTime(value, "event date and time");
        return parseUsing(normalized, List.of(ISO_DATE_TIME, SHORT_DATE_TIME))
                .orElseThrow(() -> new TaskValidationException(
                        "That event date and time is invalid. Use yyyy-MM-dd HHmm or d/M/yyyy HHmm."));
    }

    /** Attempts to parse a stored event boundary, including canonical storage values. */
    public static Optional<LocalDateTime> tryParseEventBoundary(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return parseUsing(value.strip(), EVENT_FORMATS);
    }

    /** Builds a locale-stable formatter that rejects invalid calendar values. */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /** Trims a date-time value and rejects missing or unsafe content. */
    private static String normalizeDateTime(String value, String fieldName) {
        return InputValidator.normalizeStoredText(value, fieldName, 40);
    }

    /** Attempts each formatter without allowing a failed format to escape. */
    private static Optional<LocalDateTime> parseUsing(String value, List<DateTimeFormatter> formats) {
        for (DateTimeFormatter format : formats) {
            try {
                return Optional.of(LocalDateTime.parse(value, format));
            } catch (DateTimeParseException ignored) {
                // Try the next supported representation.
            }
        }
        return Optional.empty();
    }
}
