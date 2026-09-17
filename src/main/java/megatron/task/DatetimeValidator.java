package megatron.task;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses supported user date/time formats into local date/time values.
 */
public final class DatetimeValidator {
    private static final String DEFAULT_OUTPUT_PATTERN = "dd MMM uu, HHmm'hrs'";
    private static final Map<String, DayOfWeek> WEEKDAYS = Map.ofEntries(
            Map.entry("mon", DayOfWeek.MONDAY), Map.entry("monday", DayOfWeek.MONDAY),
            Map.entry("tue", DayOfWeek.TUESDAY), Map.entry("tuesday", DayOfWeek.TUESDAY),
            Map.entry("wed", DayOfWeek.WEDNESDAY), Map.entry("wednesday", DayOfWeek.WEDNESDAY),
            Map.entry("thu", DayOfWeek.THURSDAY), Map.entry("thursday", DayOfWeek.THURSDAY),
            Map.entry("fri", DayOfWeek.FRIDAY), Map.entry("friday", DayOfWeek.FRIDAY),
            Map.entry("sat", DayOfWeek.SATURDAY), Map.entry("saturday", DayOfWeek.SATURDAY),
            Map.entry("sun", DayOfWeek.SUNDAY), Map.entry("sunday", DayOfWeek.SUNDAY));
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE
            .withResolverStyle(ResolverStyle.STRICT);
    private static final List<DateTimeFormatter> SUPPORTED_DATE_TIME_FORMATTERS = List.of(
            strictFormatter("d/M/uuuu HHmm"),
            strictFormatter("d/M/uuuu H:mm"),
            strictFormatter("d/M/uuuu ha"),
            strictFormatter("d/M/uuuu h:mma"),
            textFormatter("MMM d uuuu HHmm"),
            textFormatter("MMM d uuuu H:mm"),
            textFormatter("MMM d uuuu h:mma"),
            textFormatter("MMM d uuuu ha"),
            textFormatter("MMMM d uuuu HHmm"),
            textFormatter("MMMM d uuuu H:mm"),
            textFormatter("MMMM d uuuu h:mma"),
            textFormatter("MMMM d uuuu ha"),
            textFormatter("d MMM uuuu HHmm"),
            textFormatter("d MMM uuuu H:mm"),
            textFormatter("d MMM uuuu h:mma"),
            textFormatter("d MMM uuuu ha"),
            textFormatter("d MMMM uuuu HHmm"),
            textFormatter("d MMMM uuuu H:mm"),
            textFormatter("d MMMM uuuu h:mma"),
            textFormatter("d MMMM uuuu ha"));
    private static final List<DateTimeFormatter> SUPPORTED_DATE_ONLY_FORMATTERS = List.of(
            strictFormatter("d/M/uuuu"),
            textFormatter("MMM d uuuu"),
            textFormatter("MMMM d uuuu"),
            textFormatter("d MMM uuuu"),
            textFormatter("d MMMM uuuu"));
    private static final List<DateTimeFormatter> SUPPORTED_TIME_FORMATTERS = List.of(
            strictFormatter("HHmm"),
            strictFormatter("H:mm"),
            strictFormatter("ha"),
            strictFormatter("h:mma"));
    private static final Pattern TEXT_MONTH_WITHOUT_YEAR = Pattern.compile(
            "(?i)^(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|"
                    + "Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|"
                    + "Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\s+"
                    + "(\\d{1,2})(?:\\s+(.*))?$");

    private DatetimeValidator() {
    }

    /**
     * Converts a supported date/time string to a local date/time value.
     *
     * @param input The supported date/time string.
     * @return The parsed local date/time value.
     * @throws IllegalArgumentException If the input is invalid.
     */
    public static LocalDateTime parseToLocalDateTime(String input) {
        return parse(input).value;
    }

    /**
     * Returns whether the input explicitly included a time.
     *
     * @param input The supported date/time string.
     * @return True if the input includes a time.
     * @throws IllegalArgumentException If the input is invalid.
     */
    public static boolean hasExplicitTime(String input) {
        return parse(input).isTimeSpecified;
    }

    /**
     * Formats a local date/time using the supplied DateTimeFormatter pattern.
     *
     * @param value The local date/time value.
     * @param pattern The DateTimeFormatter pattern.
     * @return The formatted date/time string.
     */
    public static String format(LocalDateTime value, String pattern) {
        return value.format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
    }

    /**
     * Formats a local date/time using the chatbot's default numeric 24-hour format.
     *
     * @param value The local date/time value.
     * @return The formatted date/time string.
     */
    public static String formatForUser(LocalDateTime value) {
        return format(value, DEFAULT_OUTPUT_PATTERN);
    }

    /**
     * Parses a user input and records whether its time was explicitly supplied.
     *
     * @param input The user date/time input.
     * @return The parsed date/time and time-presence flag.
     * @throws IllegalArgumentException If the input is invalid.
     */
    private static ParsedDateTime parse(String input) {
        if (input == null || input.isBlank()) {
            throw invalidDateTime();
        }

        String value = normalizeInput(input);
        ParsedDateTime weekdayDateTime = parseWeekday(value);
        if (weekdayDateTime != null) {
            return weekdayDateTime;
        }

        // Try an explicit year before adding a default year for text-month inputs.
        ParsedDateTime dateTime = tryParseDateTime(value);
        if (dateTime != null) {
            return dateTime;
        }

        String valueWithYear = addCurrentYearIfMissing(value);
        if (valueWithYear != null) {
            dateTime = tryParseDateTime(valueWithYear);
            if (dateTime != null) {
                return dateTime;
            }
        }

        throw invalidDateTime();
    }

    /**
     * Tries the supported date/time and date-only formatters.
     *
     * @param value The normalized date/time input.
     * @return The parsed date/time, or null if no formatter matches.
     */
    private static ParsedDateTime tryParseDateTime(String value) {
        ParsedDateTime isoDateTime = tryParseIsoDateTime(value);
        if (isoDateTime != null) {
            return isoDateTime;
        }

        for (DateTimeFormatter formatter : SUPPORTED_DATE_TIME_FORMATTERS) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(value, formatter), true);
            } catch (DateTimeParseException ignored) {
                continue;
            }
        }
        for (DateTimeFormatter formatter : SUPPORTED_DATE_ONLY_FORMATTERS) {
            try {
                return new ParsedDateTime(LocalDate.parse(value, formatter).atStartOfDay(), false);
            } catch (DateTimeParseException ignored) {
                continue;
            }
        }
        try {
            return new ParsedDateTime(LocalDate.parse(value, ISO_DATE).atStartOfDay(), false);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    /**
     * Parses an ISO date followed by one of the supported time formats.
     *
     * @param value The normalized ISO date/time input.
     * @return The parsed date/time, or null if the input is not ISO date/time.
     */
    private static ParsedDateTime tryParseIsoDateTime(String value) {
        if (value.length() <= 10 || value.charAt(10) != ' ') {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(value.substring(0, 10), ISO_DATE);
            return new ParsedDateTime(date.atTime(parseTime(value.substring(11))), true);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    /**
     * Adds the current year only to a text-month input that does not have a year.
     *
     * @param value The normalized date/time input.
     * @return The input with a year, or null if a year is already present.
     */
    private static String addCurrentYearIfMissing(String value) {
        Matcher matcher = TEXT_MONTH_WITHOUT_YEAR.matcher(value);
        if (!matcher.matches()) {
            return null;
        }
        String time = matcher.group(3);
        return matcher.group(1) + " " + matcher.group(2) + " " + LocalDate.now().getYear()
                + (time == null ? "" : " " + time);
    }

    /**
     * Resolves a weekday to its next occurrence. A missing time means midnight.
     *
     * @param value The weekday input, with an optional time.
     * @return The next matching date/time, or null if the input is not a weekday.
     * @throws IllegalArgumentException If the weekday time is invalid.
     */
    private static ParsedDateTime parseWeekday(String value) {
        String[] parts = value.toLowerCase(Locale.ENGLISH).split("\\s+", 2);
        DayOfWeek weekday = WEEKDAYS.get(parts[0]);
        if (weekday == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        int daysUntil = weekday.getValue() - today.getDayOfWeek().getValue();
        if (daysUntil < 0) {
            daysUntil += 7;
        }
        boolean isTimeSpecified = parts.length > 1;
        LocalTime time = isTimeSpecified ? parseTime(parts[1]) : LocalTime.MIDNIGHT;
        if (daysUntil == 0 && !today.atTime(time).isAfter(now)) {
            daysUntil = 7;
        }
        return new ParsedDateTime(today.plusDays(daysUntil).atTime(time), isTimeSpecified);
    }

    /**
     * Parses one of the supported time formats.
     *
     * @param value The time input.
     * @return The parsed local time.
     * @throws IllegalArgumentException If the time is invalid.
     */
    private static LocalTime parseTime(String value) {
        String normalized = value.trim().replaceAll("\\s+", "").toUpperCase(Locale.ENGLISH);
        for (DateTimeFormatter formatter : SUPPORTED_TIME_FORMATTERS) {
            try {
                return LocalTime.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
                continue;
            }
        }
        throw invalidDateTime();
    }

    /**
     * Normalizes whitespace, ordinal day suffixes, and spaces before am/pm.
     *
     * @param input The raw date/time input.
     * @return The normalized date/time input.
     */
    private static String normalizeInput(String input) {
        String value = input.trim().replaceAll("\\s+", " ")
                .replaceAll("(?i)(\\d+)(st|nd|rd|th)\\b", "$1");
        return value.replaceAll("(?i)(\\d{1,2}(?::\\d{2})?)\\s+(am|pm)\\b", "$1$2");
    }

    /**
     * Creates a case-insensitive formatter that rejects invalid dates and times.
     *
     * @param pattern The DateTimeFormatter pattern.
     * @return The strict date/time formatter.
     */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(pattern)
                .toFormatter(Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Creates a strict formatter for a pattern that contains month text.
     *
     * @param pattern The DateTimeFormatter pattern.
     * @return The strict text-month formatter.
     */
    private static DateTimeFormatter textFormatter(String pattern) {
        return strictFormatter(pattern);
    }

    /**
     * Creates the standard exception for unsupported or invalid date/time input.
     *
     * @return The invalid date/time exception.
     */
    private static IllegalArgumentException invalidDateTime() {
        return new IllegalArgumentException("Invalid date/time. Use datetime-help to view supported "
                + "date/time formats.");
    }

    /**
     * Holds the parsed value and whether the user supplied a time.
     */
    private static final class ParsedDateTime {
        private final LocalDateTime value;
        private final boolean isTimeSpecified;

        /**
         * Creates parsed date/time information.
         *
         * @param value The parsed local date/time value.
         * @param isTimeSpecified Whether the input explicitly included a time.
         */
        private ParsedDateTime(LocalDateTime value, boolean isTimeSpecified) {
            assert value != null : "Parsed date/time information must contain a value.";
            this.value = value;
            this.isTimeSpecified = isTimeSpecified;
        }
    }
}
