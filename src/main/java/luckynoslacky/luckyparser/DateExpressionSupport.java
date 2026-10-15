package luckynoslacky.luckyparser;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores the lexical definitions used when resolving date expressions.
 */
final class DateExpressionSupport {
    private static final Pattern DAY_ONLY_PATTERN = Pattern.compile(
            "^(\\d{1,2})$");
    private static final Pattern YEAR_ONLY_PATTERN = Pattern.compile(
            "^(\\d{4})$");
    private static final Pattern MONTH_DAY_PATTERN = Pattern.compile(
            "^([A-Za-z]+)\\s+(\\d{1,2})(?:\\s+(\\d{4}))?$");
    private static final Pattern DAY_MONTH_PATTERN = Pattern.compile(
            "^(\\d{1,2})\\s+([A-Za-z]+)(?:\\s+(\\d{4}))?$");

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            formatter("uuuu-MM-dd"),
            formatter("uuuu/MM/dd"),
            formatter("d/M/uuuu"),
            formatter("d-M-uuuu"),
            formatter("d MMM uuuu"),
            formatter("d MMMM uuuu"),
            formatter("MMM d uuuu"),
            formatter("MMMM d uuuu"));

    /** Associates accepted weekday names with their java.time values. */
    private enum Weekday {
        MONDAY("monday", DayOfWeek.MONDAY),
        TUESDAY("tuesday", DayOfWeek.TUESDAY),
        WEDNESDAY("wednesday", DayOfWeek.WEDNESDAY),
        THURSDAY("thursday", DayOfWeek.THURSDAY),
        FRIDAY("friday", DayOfWeek.FRIDAY),
        SATURDAY("saturday", DayOfWeek.SATURDAY),
        SUNDAY("sunday", DayOfWeek.SUNDAY);

        private final String fullName;
        private final DayOfWeek dayOfWeek;

        Weekday(String fullName, DayOfWeek dayOfWeek) {
            this.fullName = fullName;
            this.dayOfWeek = dayOfWeek;
        }

        private boolean matches(String value) {
            return value.equals(fullName)
                    || value.equals(fullName.substring(0, 3));
        }
    }

    /** Associates accepted month names with their one-based month numbers. */
    private enum Month {
        JANUARY("january", 1),
        FEBRUARY("february", 2),
        MARCH("march", 3),
        APRIL("april", 4),
        MAY("may", 5),
        JUNE("june", 6),
        JULY("july", 7),
        AUGUST("august", 8),
        SEPTEMBER("september", 9),
        OCTOBER("october", 10),
        NOVEMBER("november", 11),
        DECEMBER("december", 12);

        private final String fullName;
        private final int number;

        Month(String fullName, int number) {
            this.fullName = fullName;
            this.number = number;
        }

        private boolean matches(String value) {
            return value.equals(fullName)
                    || value.equals(fullName.substring(0, 3));
        }
    }

    private DateExpressionSupport() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Checks whether text contains only a four-digit year.
     *
     * @param dateText normalized date text
     * @return true if the text is a year-only expression
     */
    static boolean isYearOnly(String dateText) {
        return YEAR_ONLY_PATTERN.matcher(dateText).matches();
    }

    /**
     * Matches a month/day expression with an optional year.
     *
     * @param dateText date text to inspect
     * @return matcher for the month/day expression
     */
    static Matcher matchMonthDay(String dateText) {
        return MONTH_DAY_PATTERN.matcher(dateText);
    }

    /**
     * Matches a day/month expression with an optional year.
     *
     * @param dateText date text to inspect
     * @return matcher for the day/month expression
     */
    static Matcher matchDayMonth(String dateText) {
        return DAY_MONTH_PATTERN.matcher(dateText);
    }

    /**
     * Matches a day-only expression.
     *
     * @param dateText date text to inspect
     * @return matcher for the day-only expression
     */
    static Matcher matchDayOnly(String dateText) {
        return DAY_ONLY_PATTERN.matcher(dateText);
    }

    /**
     * Normalizes date text, including ordinal suffixes and the word "the".
     *
     * @param rawDateText raw date text
     * @return normalized lower-case date text
     */
    static String normalizeDateText(String rawDateText) {
        return rawDateText.toLowerCase(Locale.ENGLISH)
                .replaceAll("(?i)\\b(\\d{1,2})(st|nd|rd|th)\\b", "$1")
                .replaceAll("(?i)\\bthe\\b", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Removes an optional weekday prefix from a date expression.
     *
     * @param dateExpression date expression that may begin with a weekday
     * @return expression without the weekday prefix
     */
    static String removeWeekdayPrefix(String dateExpression) {
        return dateExpression.replaceFirst(
                "^(monday|tuesday|wednesday|thursday|friday|saturday|sunday"
                        + "|mon|tue|wed|thu|fri|sat|sun)\\s+",
                "");
    }

    /**
     * Finds the first explicitly supported absolute date format that matches.
     *
     * @param normalizedDateText normalized date text
     * @return parsed date, or null if no formatter accepts the text
     */
    static LocalDate findKnownFormat(String normalizedDateText) {
        return DATE_FORMATTERS.stream()
                .map(formatter -> applyFormatter(normalizedDateText, formatter))
                .flatMap(Optional::stream)
                .filter(date -> date.getYear() > 0)
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds the day value represented by a weekday name or abbreviation.
     *
     * @param weekdayText weekday text
     * @return matching day, or null if the text is not a weekday
     */
    static DayOfWeek findWeekday(String weekdayText) {
        return Arrays.stream(Weekday.values())
                .filter(weekday -> weekday.matches(weekdayText))
                .map(weekday -> weekday.dayOfWeek)
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds the month number represented by a month name or abbreviation.
     *
     * @param monthText month text
     * @return month number from 1 to 12, or -1 if not a month
     */
    static int findMonthNumber(String monthText) {
        String normalized = monthText.toLowerCase(Locale.ENGLISH);
        return Arrays.stream(Month.values())
                .filter(month -> month.matches(normalized))
                .mapToInt(month -> month.number)
                .findFirst()
                .orElse(-1);
    }

    /**
     * Applies one formatter to the normalized date text.
     *
     * @param normalizedDateText normalized date text to parse
     * @param formatter formatter to apply
     * @return parsed date, or an empty Optional if parsing fails
     */
    private static Optional<LocalDate> applyFormatter(
            String normalizedDateText, DateTimeFormatter formatter) {
        try {
            return Optional.of(LocalDate.parse(normalizedDateText, formatter));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    /**
     * Creates a strict, case-insensitive English date formatter.
     *
     * @param pattern formatter pattern
     * @return configured formatter
     */
    private static DateTimeFormatter formatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
