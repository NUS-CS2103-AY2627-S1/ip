package edith.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Parses and formats the date and time text used by Edith's dated tasks. */
public final class DateTimeParser {
    private static final DateTimeFormatter DATE_TIME_INPUT = DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_INPUT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter[] NATURAL_DATE_TIME_INPUTS = {
        createNaturalFormatter("MMMM d uuuu ha"),
        createNaturalFormatter("MMM d uuuu ha"),
        createNaturalFormatter("MMMM d uuuu h:mma"),
        createNaturalFormatter("MMM d uuuu h:mma")
    };
    private static final DateTimeFormatter[] NATURAL_DATE_INPUTS = {
        createNaturalFormatter("MMMM d uuuu"),
        createNaturalFormatter("MMM d uuuu")
    };
    private static final DateTimeFormatter[] MONTH_DAY_TIME_INPUTS = {
        createNaturalFormatter("MMMM d ha"),
        createNaturalFormatter("MMM d ha"),
        createNaturalFormatter("MMMM d h:mma"),
        createNaturalFormatter("MMM d h:mma")
    };
    private static final DateTimeFormatter[] MONTH_DAY_INPUTS = {
        createNaturalFormatter("MMMM d"),
        createNaturalFormatter("MMM d")
    };
    private static final DateTimeFormatter DATE_OUTPUT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_OUTPUT =
            DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);

    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /** Returns null for legacy free-form text that is not a supported date format. */
    public static LocalDateTime parse(String text) {
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(text, DATE_TIME_INPUT);
            } catch (DateTimeParseException ignoredTime) {
                try {
                    return LocalDate.parse(text, DATE_INPUT).atStartOfDay();
                } catch (DateTimeParseException ignoredDate) {
                    return null;
                }
            }
        }
    }

    /**
     * Parses a date for chronological sorting, including common month-name formats.
     *
     * <p>Dates without a year use the current year. Ordinal suffixes such as {@code th} are ignored.</p>
     *
     * @param text date text supplied by the user
     * @return parsed date-time, or null when the text has no supported date
     */
    public static LocalDateTime parseForSorting(String text) {
        LocalDateTime parsedDateTime = parse(text);
        if (parsedDateTime != null) {
            return parsedDateTime;
        }

        String normalizedText = text.trim().replaceAll("(?i)(?<=\\d)(st|nd|rd|th)\\b", "");
        parsedDateTime = parseNaturalDateTime(normalizedText, NATURAL_DATE_TIME_INPUTS);
        if (parsedDateTime != null) {
            return parsedDateTime;
        }

        parsedDateTime = parseNaturalDate(normalizedText, NATURAL_DATE_INPUTS);
        if (parsedDateTime != null) {
            return parsedDateTime;
        }

        parsedDateTime = parseMonthDayTime(normalizedText);
        return parsedDateTime == null ? parseMonthDay(normalizedText) : parsedDateTime;
    }

    /**
     * Parses a time without a date.
     *
     * @param text time text supplied by the user
     * @return parsed time, or null when the text is not a supported time
     */
    public static LocalTime parseTimeOnly(String text) {
        DateTimeFormatter[] formatters = {
            createNaturalFormatter("ha"),
            createNaturalFormatter("h:mma")
        };
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalTime.parse(text, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        return null;
    }

    /** Formats a parsed date-time for display. */
    public static String format(LocalDateTime dateTime) {
        assert dateTime != null : "Date-time to format must not be null";
        String date = dateTime.format(DATE_OUTPUT);
        return dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? date
                : date + " " + dateTime.format(TIME_OUTPUT).toLowerCase(Locale.ROOT);
    }

    /** Creates a case-insensitive formatter for a natural-language date pattern. */
    private static DateTimeFormatter createNaturalFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /** Parses a complete natural-language date and time. */
    private static LocalDateTime parseNaturalDateTime(String text, DateTimeFormatter[] formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        return null;
    }

    /** Parses a complete natural-language date. */
    private static LocalDateTime parseNaturalDate(String text, DateTimeFormatter[] formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(text, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        return null;
    }

    /** Parses a month, day, and time using the current year. */
    private static LocalDateTime parseMonthDayTime(String text) {
        for (DateTimeFormatter formatter : MONTH_DAY_TIME_INPUTS) {
            try {
                MonthDay monthDay = MonthDay.from(formatter.parse(text));
                LocalTime time = LocalTime.from(formatter.parse(text));
                return monthDay.atYear(LocalDate.now().getYear()).atTime(time);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        return null;
    }

    /** Parses a month and day using the current year. */
    private static LocalDateTime parseMonthDay(String text) {
        for (DateTimeFormatter formatter : MONTH_DAY_INPUTS) {
            try {
                MonthDay monthDay = MonthDay.parse(text, formatter);
                return monthDay.atYear(LocalDate.now().getYear()).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        return null;
    }
}
