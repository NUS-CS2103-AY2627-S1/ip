package crystal.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a task's date, time, or unrestricted text such as {@code Monday}.
 */
public final class TaskDateTime {
    /** Strict formatter for day/month/four-digit-year dates. */
    private static final DateTimeFormatter SLASH_DATE_FORMATTER =
            createDateFormatter("d/M/uuuu");

    /** Accepted date formats for commands and previously saved task data. */
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            createDateFormatter("uuuu-M-d"),
            SLASH_DATE_FORMATTER);

    /** Month-name date formats used after spacing variations are normalized. */
    private static final List<DateTimeFormatter> MONTH_NAME_DATE_FORMATTERS = List.of(
            createDateFormatter("d MMM uuuu"),
            createDateFormatter("d MMMM uuuu"));

    /** Day/month dates whose two-digit year should be interpreted as 20xx. */
    private static final Pattern TWO_DIGIT_SLASH_DATE_PATTERN =
            Pattern.compile("^(\\d{1,2})/(\\d{1,2})/(\\d{2})$");

    /**
     * Day-first month-name dates with optional spaces, separators, and ordinal suffixes.
     * Examples include 2Oct2026, 2 Dec2026, 2Nov 2026, and 2nd-Oct-2026.
     */
    private static final Pattern FLEXIBLE_MONTH_NAME_DATE_PATTERN = Pattern.compile(
            "^(\\d{1,2})(?:st|nd|rd|th)?[\\s./-]*([A-Za-z]+)"
                    + "[\\s,./-]*(\\d{2}(?:\\d{2})?)$",
            Pattern.CASE_INSENSITIVE);

    /** Consistent date format used for display and storage. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH);

    /** Consistent 24-hour clock format used for display and storage. */
    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HHmm");

    /** Times such as 6am, 630pm, and 6.30pm. */
    private static final Pattern TWELVE_HOUR_TIME_PATTERN = Pattern.compile(
            "^(\\d{1,2})(?:(?:[.:](\\d{2}))|(\\d{2}))?([aApP][mM])$");

    /** Times such as 18:30 and 18.30. */
    private static final Pattern SEPARATED_TIME_PATTERN =
            Pattern.compile("^(\\d{1,2})[.:](\\d{2})$");

    /** Times such as 630 and 1830. */
    private static final Pattern COMPACT_TIME_PATTERN =
            Pattern.compile("^(\\d{1,2})(\\d{2})$");

    /** Unrestricted text retained when the input is not a complete date. */
    private final String text;

    /** Parsed calendar date, or {@code null} when no date was supplied. */
    private final LocalDate date;

    /** Parsed clock time, or {@code null} when no time was supplied. */
    private final LocalTime time;

    /**
     * Creates a date/time value from its parsed components.
     *
     * @param text unrestricted text to preserve.
     * @param date parsed calendar date.
     * @param time parsed clock time.
     */
    private TaskDateTime(String text, LocalDate date, LocalTime time) {
        this.text = text;
        this.date = date;
        this.time = time;
    }

    /**
     * Parses supported dates and times while preserving all other input as text.
     * A final time token is still recognized after text, so {@code Monday 6am}
     * becomes {@code Monday 0600}.
     *
     * @param input date, time, or unrestricted scheduling text.
     * @return parsed task date/time value.
     */
    public static TaskDateTime parse(String input) {
        String value = input.trim();
        LocalDate parsedDate = parseDate(value);
        if (parsedDate != null) {
            return new TaskDateTime(null, parsedDate, null);
        }
        LocalTime parsedTime = parseTime(value);
        if (parsedTime != null) {
            return new TaskDateTime(null, null, parsedTime);
        }

        int lastSpaceIndex = value.lastIndexOf(' ');
        if (lastSpaceIndex > 0) {
            String firstPart = value.substring(0, lastSpaceIndex).trim();
            String lastPart = value.substring(lastSpaceIndex + 1).trim();
            parsedDate = parseDate(firstPart);
            parsedTime = parseTime(lastPart);
            if (parsedDate != null && parsedTime != null) {
                return new TaskDateTime(null, parsedDate, parsedTime);
            }
            if (parsedTime != null) {
                return new TaskDateTime(firstPart, null, parsedTime);
            }
        }
        return new TaskDateTime(value, null, null);
    }

    /**
     * Returns a normalized date/time while retaining any unrestricted text.
     *
     * @return date as {@code dd MMM yyyy}, time as {@code HHmm}, or original text.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (text != null) {
            result.append(text);
        }
        if (date != null) {
            appendPart(result, date.format(DISPLAY_DATE_FORMATTER));
        }
        if (time != null) {
            appendPart(result, time.format(DISPLAY_TIME_FORMATTER));
        }
        return result.toString();
    }

    /**
     * Parses a date using each supported command and storage format.
     *
     * @param input possible date.
     * @return parsed date, or {@code null} when the value is not a supported date.
     */
    public static LocalDate parseDate(String input) {
        String value = input.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException exception) {
                // Try the next supported date format.
            }
        }

        Matcher slashDateMatcher = TWO_DIGIT_SLASH_DATE_PATTERN.matcher(value);
        if (slashDateMatcher.matches()) {
            String normalizedDate = slashDateMatcher.group(1) + "/"
                    + slashDateMatcher.group(2) + "/20" + slashDateMatcher.group(3);
            try {
                return LocalDate.parse(normalizedDate, SLASH_DATE_FORMATTER);
            } catch (DateTimeParseException exception) {
                return null;
            }
        }

        Matcher monthNameMatcher = FLEXIBLE_MONTH_NAME_DATE_PATTERN.matcher(value);
        if (monthNameMatcher.matches()) {
            String year = expandYear(monthNameMatcher.group(3));
            String normalizedDate = monthNameMatcher.group(1) + " "
                    + monthNameMatcher.group(2) + " " + year;
            for (DateTimeFormatter formatter : MONTH_NAME_DATE_FORMATTERS) {
                try {
                    return LocalDate.parse(normalizedDate, formatter);
                } catch (DateTimeParseException exception) {
                    // Try the next supported month-name format.
                }
            }
        }
        return null;
    }

    /**
     * Returns whether this value contains the specified calendar date.
     *
     * @param targetDate date to compare.
     * @return {@code true} if this value contains the target date.
     */
    public boolean occursOn(LocalDate targetDate) {
        return date != null && date.equals(targetDate);
    }

    /**
     * Returns the parsed calendar date, if this value contains one.
     *
     * @return parsed date, or {@code null} when this value has no calendar date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Formats a calendar date using Crystal's display and storage format.
     *
     * @param date date to format.
     * @return date formatted as {@code dd MMM yyyy}.
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    /**
     * Expands a two-digit year into the 2000s while retaining a four-digit year.
     *
     * @param year two-digit or four-digit year.
     * @return four-digit year.
     */
    private static String expandYear(String year) {
        return year.length() == 2 ? "20" + year : year;
    }

    /**
     * Parses supported 12-hour and 24-hour clock formats.
     *
     * @param value possible time.
     * @return parsed time, or {@code null} when the value is not a supported time.
     */
    private static LocalTime parseTime(String value) {
        Matcher twelveHourMatcher = TWELVE_HOUR_TIME_PATTERN.matcher(value);
        if (twelveHourMatcher.matches()) {
            int hour = Integer.parseInt(twelveHourMatcher.group(1));
            String minuteText = twelveHourMatcher.group(2) != null
                    ? twelveHourMatcher.group(2) : twelveHourMatcher.group(3);
            int minute = minuteText == null ? 0 : Integer.parseInt(minuteText);
            if (hour < 1 || hour > 12 || minute > 59) {
                return null;
            }
            if (hour == 12) {
                hour = 0;
            }
            if (twelveHourMatcher.group(4).equalsIgnoreCase("pm")) {
                hour += 12;
            }
            return LocalTime.of(hour, minute);
        }

        Matcher separatedTimeMatcher = SEPARATED_TIME_PATTERN.matcher(value);
        if (separatedTimeMatcher.matches()) {
            return createTwentyFourHourTime(
                    separatedTimeMatcher.group(1), separatedTimeMatcher.group(2));
        }
        Matcher compactTimeMatcher = COMPACT_TIME_PATTERN.matcher(value);
        if (compactTimeMatcher.matches()) {
            return createTwentyFourHourTime(
                    compactTimeMatcher.group(1), compactTimeMatcher.group(2));
        }
        return null;
    }

    /**
     * Creates a time after validating its 24-hour clock fields.
     *
     * @param hourText hour digits.
     * @param minuteText minute digits.
     * @return parsed time, or {@code null} when either field is out of range.
     */
    private static LocalTime createTwentyFourHourTime(String hourText, String minuteText) {
        int hour = Integer.parseInt(hourText);
        int minute = Integer.parseInt(minuteText);
        if (hour > 23 || minute > 59) {
            return null;
        }
        return LocalTime.of(hour, minute);
    }

    /**
     * Creates a strict, case-insensitive date formatter.
     *
     * @param pattern date pattern.
     * @return configured date formatter.
     */
    private static DateTimeFormatter createDateFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Appends a formatted component with one separating space when needed.
     *
     * @param result destination text.
     * @param part formatted date or time.
     */
    private static void appendPart(StringBuilder result, String part) {
        if (!result.isEmpty()) {
            result.append(' ');
        }
        result.append(part);
    }
}
