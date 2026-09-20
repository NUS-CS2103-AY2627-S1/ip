package larper.task;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Parses and formats date and time text used in deadline and event tasks.
 */
public class TaskDateTimeParser {
    private static final String NO_TIME = "no time";
    private static final String NO_TIME_SUFFIX = " " + NO_TIME;
    private static final String BY_MARKER = "by ";
    private static final String FROM_MARKER = "from ";
    private static final String TO_MARKER = "to ";
    private static final String AM_SUFFIX = "am";
    private static final String PM_SUFFIX = "pm";
    private static final String NUMERIC_TIME_PATTERN = "\\d{3,4}";
    private static final String COLON_TIME_PATTERN = "\\d{1,2}:\\d{2}";
    private static final String AM_PM_TIME_PATTERN = "\\d{1,2}(:\\d{2})?(" + AM_SUFFIX + "|" + PM_SUFFIX + ")";
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy",
            Locale.ENGLISH);
    private static final DateTimeFormatter SLASH_DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final String[][] MONTH_ALIASES = {
        {"jan", "jan"},
        {"january", "jan"},
        {"janurary", "jan"},
        {"feb", "feb"},
        {"february", "feb"},
        {"feburary", "feb"},
        {"mar", "mar"},
        {"march", "mar"},
        {"apr", "apr"},
        {"april", "apr"},
        {"may", "may"},
        {"jun", "jun"},
        {"june", "jun"},
        {"jul", "jul"},
        {"july", "jul"},
        {"aug", "aug"},
        {"august", "aug"},
        {"sep", "sep"},
        {"sept", "sep"},
        {"september", "sep"},
        {"oct", "oct"},
        {"october", "oct"},
        {"nov", "nov"},
        {"november", "nov"},
        {"dec", "dec"},
        {"december", "dec"}
    };
    private static final DateTimeFormatter SHORT_MONTH_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMM yyyy")
            .toFormatter(Locale.ENGLISH);
    private static final DateTimeFormatter LONG_MONTH_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMMM yyyy")
            .toFormatter(Locale.ENGLISH);
    private static final DateTimeFormatter SHORT_MONTH_FIRST_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMM d yyyy")
            .toFormatter(Locale.ENGLISH);
    private static final DateTimeFormatter LONG_MONTH_FIRST_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMMM d yyyy")
            .toFormatter(Locale.ENGLISH);
    private static final DateTimeFormatter[] MONTH_DATE_FORMATTERS = {
        SHORT_MONTH_DATE_FORMATTER,
        LONG_MONTH_DATE_FORMATTER,
        SHORT_MONTH_FIRST_DATE_FORMATTER,
        LONG_MONTH_FIRST_DATE_FORMATTER
    };

    /**
     * Returns the parsed date and time from a task date-time string.
     * If no time is present, the specified default time is used.
     *
     * @throws RuntimeException If the date or time cannot be parsed.
     */
    public static TaskDateTime parse(String text, String defaultTime) {
        assert text != null : "Date-time parser should receive date-time text.";
        assert defaultTime != null : "Date-time parser should receive a default time.";
        String trimmedText = removeLeadingDateMarker(text.trim());
        ensureDateIsPresent(trimmedText);

        DateTimeText dateTimeText = splitDateAndTime(trimmedText, defaultTime);
        return new TaskDateTime(parseDate(dateTimeText.dateText), dateTimeText.time);
    }

    /**
     * Returns the parsed date from supported task date formats.
     *
     * @throws RuntimeException If the date cannot be parsed.
     */
    public static LocalDate parseDate(String dateText) {
        assert dateText != null : "Date parser should receive date text.";
        String cleanDateText = cleanDateText(dateText);
        try {
            return LocalDate.parse(cleanDateText);
        } catch (DateTimeParseException e) {
            return parseNonIsoDate(cleanDateText);
        }
    }

    /**
     * Returns a normalized task time in 24-hour HHmm format.
     * The special value "no time" is returned unchanged.
     *
     * @throws IllegalArgumentException If the time cannot be parsed.
     */
    public static String normalizeTime(String timeText) {
        assert timeText != null : "Time parser should receive time text.";
        String cleanTimeText = timeText.trim().toLowerCase();
        if (isNoTimeOnly(cleanTimeText)) {
            return NO_TIME;
        }
        if (cleanTimeText.matches(NUMERIC_TIME_PATTERN)) {
            String normalizedTime = String.format("%04d", Integer.parseInt(cleanTimeText));
            assert isNormalizedTime(normalizedTime) : "Numeric time should be normalized to HHmm.";
            return normalizedTime;
        }
        if (cleanTimeText.matches(COLON_TIME_PATTERN)) {
            String normalizedTime = cleanTimeText.replace(":", "");
            assert isNormalizedTime(normalizedTime) : "Colon time should be normalized to HHmm.";
            return normalizedTime;
        }
        if (cleanTimeText.matches(AM_PM_TIME_PATTERN)) {
            String normalizedTime = parseAmPmTime(cleanTimeText);
            assert isNormalizedTime(normalizedTime) : "AM/PM time should be normalized to HHmm.";
            return normalizedTime;
        }
        throw new IllegalArgumentException("Invalid deadline time.");
    }

    /**
     * Returns whether the text looks like a supported task time.
     */
    public static boolean looksLikeTime(String text) {
        String cleanTimeText = text.trim().toLowerCase();
        return cleanTimeText.matches(NUMERIC_TIME_PATTERN) || cleanTimeText.matches(COLON_TIME_PATTERN)
                || cleanTimeText.matches(AM_PM_TIME_PATTERN);
    }

    /**
     * Returns whether the text is a valid answer to an optional time prompt.
     */
    public static boolean isValidTimeAnswer(String text) {
        return isNoTimeOnly(text) || looksLikeTime(text);
    }

    /**
     * Returns whether the text contains a supported month name or month abbreviation.
     */
    public static boolean containsMonthName(String text) {
        String cleanText = cleanDateText(text).toLowerCase();
        int index = 0;
        while (index < MONTH_ALIASES.length) {
            if (containsWord(cleanText, MONTH_ALIASES[index][0])) {
                return true;
            }
            index++;
        }
        return false;
    }

    /**
     * Returns the date formatted for Larper's console output.
     */
    public static String formatDate(LocalDate date) {
        assert date != null : "Date formatter should receive a parsed date.";
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    private static LocalDate parseNonIsoDate(String dateText) {
        String normalizedDateText = normalizeMonthNames(dateText);
        try {
            return LocalDate.parse(normalizedDateText, SLASH_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            DayOfWeek dayOfWeek = parseDayOfWeek(normalizedDateText);
            if (dayOfWeek != null) {
                return getNextDateForDayOfWeek(dayOfWeek);
            }
            return parseMonthDate(normalizedDateText);
        }
    }

    private static LocalDate getNextDateForDayOfWeek(DayOfWeek dayOfWeek) {
        assert dayOfWeek != null : "Day-of-week calculation should receive a parsed day.";
        LocalDate today = getToday();
        int daysAhead = dayOfWeek.getValue() - today.getDayOfWeek().getValue();
        if (daysAhead < 0) {
            daysAhead += 7;
        }
        return today.plusDays(daysAhead);
    }

    private static LocalDate parseMonthDate(String dateText) {
        try {
            return parseMonthDateWithYear(dateText);
        } catch (DateTimeParseException e) {
            return parseMonthDateWithYear(dateText + " " + getCurrentYear());
        }
    }

    private static LocalDate parseMonthDateWithYear(String dateText) {
        int formatterIndex = 0;
        while (formatterIndex < MONTH_DATE_FORMATTERS.length) {
            try {
                return LocalDate.parse(dateText, MONTH_DATE_FORMATTERS[formatterIndex]);
            } catch (DateTimeParseException e) {
                formatterIndex++;
            }
        }
        return LocalDate.parse(dateText, LONG_MONTH_FIRST_DATE_FORMATTER);
    }

    private static String parseAmPmTime(String timeText) {
        assert timeText != null && timeText.matches(AM_PM_TIME_PATTERN)
                : "AM/PM parser should only receive AM/PM time text.";
        boolean isPm = timeText.endsWith(PM_SUFFIX);
        String timeWithoutPeriod = timeText.substring(0, timeText.length() - 2);
        int hour;
        int minute = 0;

        if (timeWithoutPeriod.contains(":")) {
            String[] timeParts = timeWithoutPeriod.split(":");
            hour = Integer.parseInt(timeParts[0]);
            minute = Integer.parseInt(timeParts[1]);
        } else {
            hour = Integer.parseInt(timeWithoutPeriod);
        }

        if (isPm && hour != 12) {
            hour += 12;
        } else if (!isPm && hour == 12) {
            hour = 0;
        }

        String normalizedTime = String.format("%02d%02d", hour, minute);
        assert isNormalizedTime(normalizedTime) : "AM/PM parser should return HHmm time.";
        return normalizedTime;
    }

    private static String cleanDateText(String dateText) {
        return dateText.trim()
                .replace(",", "")
                .replaceAll("(?i)\\b(\\d{1,2})(st|nd|rd|th)\\b", "$1")
                .replaceAll("\\s+", " ");
    }

    private static String normalizeMonthNames(String dateText) {
        String cleanDateText = cleanDateText(dateText).toLowerCase();
        int index = 0;
        while (index < MONTH_ALIASES.length) {
            cleanDateText = cleanDateText.replaceAll("\\b" + MONTH_ALIASES[index][0] + "\\b",
                    MONTH_ALIASES[index][1]);
            index++;
        }
        return cleanDateText;
    }

    private static boolean containsWord(String text, String word) {
        return (" " + text + " ").matches(".*\\s" + word + "\\s.*");
    }

    private static String removeLeadingDateMarker(String text) {
        String lowerText = text.toLowerCase();
        if (lowerText.startsWith(BY_MARKER)) {
            return text.substring(BY_MARKER.length()).trim();
        }
        if (lowerText.startsWith(FROM_MARKER)) {
            return text.substring(FROM_MARKER.length()).trim();
        }
        if (lowerText.startsWith(TO_MARKER)) {
            return text.substring(TO_MARKER.length()).trim();
        }
        return text;
    }

    private static DayOfWeek parseDayOfWeek(String text) {
        String trimmedText = text.trim().toLowerCase();
        switch (trimmedText) {
        case "mon":
        case "monday":
            return DayOfWeek.MONDAY;
        case "tue":
        case "tues":
        case "tuesday":
            return DayOfWeek.TUESDAY;
        case "wed":
        case "wednesday":
            return DayOfWeek.WEDNESDAY;
        case "thu":
        case "thur":
        case "thurs":
        case "thursday":
            return DayOfWeek.THURSDAY;
        case "fri":
        case "friday":
            return DayOfWeek.FRIDAY;
        case "sat":
        case "saturday":
            return DayOfWeek.SATURDAY;
        case "sun":
        case "sunday":
            return DayOfWeek.SUNDAY;
        default:
            return null;
        }
    }

    private static boolean isNoTimeOnly(String text) {
        return text.trim().equalsIgnoreCase(NO_TIME);
    }

    private static boolean endsWithNoTime(String text) {
        return text.trim().toLowerCase().endsWith(NO_TIME_SUFFIX);
    }

    private static void ensureDateIsPresent(String text) {
        if (text.isEmpty() || isNoTimeOnly(text)) {
            throw new IllegalArgumentException("Missing task date.");
        }
    }

    private static DateTimeText splitDateAndTime(String text, String defaultTime) {
        if (endsWithNoTime(text)) {
            String dateText = text.substring(0, text.length() - NO_TIME_SUFFIX.length()).trim();
            return new DateTimeText(dateText, NO_TIME);
        }

        int lastSpaceIndex = text.lastIndexOf(' ');
        if (lastSpaceIndex == -1) {
            return new DateTimeText(text, defaultTime);
        }

        String possibleDate = text.substring(0, lastSpaceIndex).trim();
        String possibleTime = text.substring(lastSpaceIndex + 1).trim();
        if (looksLikeTime(possibleTime)) {
            return new DateTimeText(possibleDate, normalizeTime(possibleTime));
        }
        return new DateTimeText(text, defaultTime);
    }

    private static boolean isNormalizedTime(String timeText) {
        return isNoTimeOnly(timeText) || timeText.matches("\\d{4}");
    }

    private static int getCurrentYear() {
        return getToday().getYear();
    }

    private static LocalDate getToday() {
        String todayProperty = System.getProperty("larper.today");
        if (todayProperty != null && !todayProperty.isBlank()) {
            return LocalDate.parse(todayProperty);
        }
        return LocalDate.now();
    }

    private static class DateTimeText {
        private String dateText;
        private String time;

        private DateTimeText(String dateText, String time) {
            this.dateText = dateText;
            this.time = time;
        }
    }
}
