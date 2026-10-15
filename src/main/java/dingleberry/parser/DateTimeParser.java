package dingleberry.parser;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dingleberry.exception.DingleberryException;

/**
 * Parses absolute dates, relative weekdays, and inherited event times.
 */
final class DateTimeParser {
    /** Defines the accepted date/time format for deadline and event input. */
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    /** Matches an optional relative qualifier and a weekday name. */
    private static final Pattern WEEKDAY_PATTERN = Pattern.compile(
            "(?i)(?:(this|next)\\s+)?"
                    + "(mon(?:day)?|tue(?:sday)?|wed(?:nesday)?|"
                    + "thu(?:rsday)?|fri(?:day)?|sat(?:urday)?|"
                    + "sun(?:day)?)(?:\\s+(.+))?");
    /** Matches a twelve-hour time with an optional colon or dot separator. */
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "(?i)(\\d{1,2})(?:(?::|\\.)(\\d{2}))?\\s*(am|pm)");
    /** Defines the number of characters used to identify a weekday. */
    private static final int WEEKDAY_PREFIX_LENGTH = 3;
    /** Defines the number of days in a week. */
    private static final int WEEKDAY_COUNT = 7;
    /** Defines the number of hours in a twelve-hour clock half. */
    private static final int TIME_HOURS_PER_HALF_DAY = 12;
    /** Defines the largest valid minute value. */
    private static final int TIME_MAX_MINUTE = 59;
    /** Defines the first valid hour on a twelve-hour clock. */
    private static final int TIME_FIRST_HOUR = 1;
    /** Defines the capture group containing a twelve-hour time suffix. */
    private static final int TIME_SUFFIX_GROUP = 3;
    /** Identifies the optional time following a relative weekday. */
    private static final int WEEKDAY_TIME_GROUP = 3;
    /** Defines the example relative weekday shown in user-facing errors. */
    private static final String RELATIVE_DATE_EXAMPLE = "next Tue";

    private DateTimeParser() {
    }

    /**
     * Parses an absolute or natural date/time into a {@link LocalDateTime}.
     *
     * @param text the raw timestamp string to parse.
     * @param referenceDate the date used to resolve relative weekdays.
     * @param inheritedDate the date used for a time-only event endpoint.
     * @return the parsed local date and time.
     * @throws DingleberryException if the supplied text is not in the expected
     *     format.
     */
    static LocalDateTime parse(final String text,
            final LocalDate referenceDate, final LocalDate inheritedDate)
            throws DingleberryException {
        String trimmedText = text.trim();
        try {
            return LocalDateTime.parse(trimmedText, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            // Continue with the natural formats after trying the legacy one.
        }

        Matcher weekdayMatcher = WEEKDAY_PATTERN.matcher(trimmedText);
        if (weekdayMatcher.matches()) {
            LocalDate weekdayDate = resolveWeekday(
                    weekdayMatcher.group(1), weekdayMatcher.group(2),
                    referenceDate);
            LocalTime time = parseOptionalTime(
                    weekdayMatcher.group(WEEKDAY_TIME_GROUP));
            return LocalDateTime.of(weekdayDate, time);
        }

        Matcher timeMatcher = TIME_PATTERN.matcher(trimmedText);
        if (timeMatcher.matches() && inheritedDate != null) {
            return LocalDateTime.of(inheritedDate, parseTime(timeMatcher));
        }

        throw createInvalidDateTimeException();
    }

    /**
     * Resolves a weekday qualifier against the supplied reference date.
     *
     * @param qualifier the optional relative qualifier.
     * @param weekdayText the weekday name to resolve.
     * @param referenceDate the date from which to resolve the weekday.
     * @return the resolved weekday date.
     * @throws DingleberryException if the weekday cannot be resolved.
     */
    private static LocalDate resolveWeekday(final String qualifier,
            final String weekdayText, final LocalDate referenceDate)
            throws DingleberryException {
        DayOfWeek targetDay = parseWeekday(weekdayText);
        int daysUntil = targetDay.getValue() - referenceDate.getDayOfWeek()
                .getValue();
        boolean isPastWeekday = daysUntil < 0;
        boolean shouldUseFollowingWeek = daysUntil == 0
                && !"this".equalsIgnoreCase(qualifier);
        if (isPastWeekday || shouldUseFollowingWeek) {
            daysUntil += WEEKDAY_COUNT;
        }
        return referenceDate.plusDays(daysUntil);
    }

    /**
     * Converts a weekday name into its corresponding {@link DayOfWeek}.
     *
     * @param weekdayText the weekday name to convert.
     * @return the corresponding day of the week.
     * @throws DingleberryException if the weekday cannot be converted.
     */
    private static DayOfWeek parseWeekday(final String weekdayText)
            throws DingleberryException {
        String normalized = weekdayText.toLowerCase(Locale.ROOT);
        return switch (normalized.substring(0, WEEKDAY_PREFIX_LENGTH)) {
            case "mon" -> DayOfWeek.MONDAY;
            case "tue" -> DayOfWeek.TUESDAY;
            case "wed" -> DayOfWeek.WEDNESDAY;
            case "thu" -> DayOfWeek.THURSDAY;
            case "fri" -> DayOfWeek.FRIDAY;
            case "sat" -> DayOfWeek.SATURDAY;
            case "sun" -> DayOfWeek.SUNDAY;
            default -> throw createInvalidDateTimeException();
        };
    }

    /**
     * Returns midnight or parses the optional time following a weekday.
     *
     * @param timeText the optional time text.
     * @return the parsed time, or midnight when no time is supplied.
     * @throws DingleberryException if the time cannot be parsed.
     */
    private static LocalTime parseOptionalTime(final String timeText)
            throws DingleberryException {
        if (timeText == null) {
            return LocalTime.MIDNIGHT;
        }
        Matcher matcher = TIME_PATTERN.matcher(timeText.trim());
        if (!matcher.matches()) {
            throw createInvalidDateTimeException();
        }
        return parseTime(matcher);
    }

    /**
     * Converts a matched twelve-hour time into a {@link LocalTime}.
     *
     * @param matcher the regular-expression match containing the time.
     * @return the parsed local time.
     * @throws DingleberryException if the time is outside the valid range.
     */
    private static LocalTime parseTime(final Matcher matcher)
            throws DingleberryException {
        int hour = Integer.parseInt(matcher.group(1));
        int minute = matcher.group(2) == null
                ? 0 : Integer.parseInt(matcher.group(2));
        if (hour < TIME_FIRST_HOUR || hour > TIME_HOURS_PER_HALF_DAY
                || minute > TIME_MAX_MINUTE) {
            throw createInvalidDateTimeException();
        }
        if ("pm".equalsIgnoreCase(matcher.group(TIME_SUFFIX_GROUP))
                && hour != TIME_HOURS_PER_HALF_DAY) {
            hour += TIME_HOURS_PER_HALF_DAY;
        } else if ("am".equalsIgnoreCase(matcher.group(TIME_SUFFIX_GROUP))
                && hour == TIME_HOURS_PER_HALF_DAY) {
            hour = 0;
        }
        return LocalTime.of(hour, minute);
    }

    /**
     * Creates the standard parser error for an unsupported date/time.
     *
     * @return the standard invalid-date exception.
     */
    private static DingleberryException createInvalidDateTimeException() {
        return new DingleberryException(
                "I couldn't understand that date/time. Use yyyy-MM-dd HHmm,"
                        + " a weekday such as Mon or " + RELATIVE_DATE_EXAMPLE
                        + ", or a time such as 2:30pm.");
    }
}
