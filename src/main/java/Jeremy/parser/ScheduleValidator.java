package jeremy.parser;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jeremy.exception.JeremyException;

/** Validates the date and time portions of free-form scheduling arguments. */
public final class ScheduleValidator {

    private static final Pattern DATE_PATTERN = Pattern.compile(
            "(?<!\\d)(\\d{1,2})/(\\d{1,2})/(\\d{4})(?!\\d)");
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "(?i)(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)\\b|(?<!\\d)(\\d{1,2}):(\\d{2})(?!\\d)");

    private ScheduleValidator() {
        // Utility class; do not instantiate.
    }

    /** Validates any explicit numeric date or clock time in the given text. */
    public static void validateDateOrTime(String value, String fieldName) throws JeremyException {
        assert value != null : "Schedule value must not be null";
        assert fieldName != null && !fieldName.isBlank() : "Schedule field name must be provided";

        validateDate(value, fieldName);
        validateTime(value, fieldName);
    }

    /** Validates that an event ends strictly after it starts when parseable. */
    public static void validateEventOrder(String from, String to) throws JeremyException {
        assert from != null && to != null : "Event bounds must not be null";

        LocalTime fromTime = extractTime(from);
        LocalTime toTime = extractTime(to);
        if (fromTime == null || toTime == null) {
            return;
        }

        LocalDate fromDate = extractDate(from);
        LocalDate toDate = extractDate(to);
        if (fromDate == null && toDate == null) {
            fromDate = LocalDate.of(2000, 1, 1);
            toDate = fromDate;
        } else if (fromDate == null) {
            fromDate = toDate;
        } else if (toDate == null) {
            toDate = fromDate;
        }

        LocalDateTime start = LocalDateTime.of(fromDate, fromTime);
        LocalDateTime end = LocalDateTime.of(toDate, toTime);
        if (!end.isAfter(start)) {
            throw new JeremyException("An event's end time must be later than its start time.");
        }
    }

    private static void validateDate(String value, String fieldName) throws JeremyException {
        Matcher matcher = DATE_PATTERN.matcher(value);
        if (!matcher.find()) {
            return;
        }
        try {
            LocalDate.of(Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1)));
        } catch (DateTimeException | NumberFormatException e) {
            throw new JeremyException("The " + fieldName + " contains a non-existent date.");
        }
    }

    private static void validateTime(String value, String fieldName) throws JeremyException {
        Matcher matcher = TIME_PATTERN.matcher(value);
        while (matcher.find()) {
            int hour;
            int minute;
            String meridiem;
            if (matcher.group(1) != null) {
                hour = Integer.parseInt(matcher.group(1));
                minute = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
                meridiem = matcher.group(3);
            } else {
                hour = Integer.parseInt(matcher.group(4));
                minute = Integer.parseInt(matcher.group(5));
                meridiem = null;
            }

            try {
                if (meridiem != null) {
                    LocalTime.parse(String.format("%02d:%02d %s", hour, minute,
                                    meridiem.toUpperCase(Locale.ROOT)),
                            java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
                } else {
                    LocalTime.of(hour, minute);
                }
            } catch (DateTimeException | NumberFormatException e) {
                throw new JeremyException("The " + fieldName + " contains an invalid time.");
            }
        }
    }

    private static LocalDate extractDate(String value) {
        Matcher matcher = DATE_PATTERN.matcher(value);
        if (!matcher.find()) {
            return null;
        }
        return LocalDate.of(Integer.parseInt(matcher.group(3)),
                Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1)));
    }

    private static LocalTime extractTime(String value) {
        Matcher matcher = TIME_PATTERN.matcher(value);
        LocalTime result = null;
        while (matcher.find()) {
            int hour = matcher.group(1) == null
                    ? Integer.parseInt(matcher.group(4)) : Integer.parseInt(matcher.group(1));
            int minute = matcher.group(1) == null
                    ? Integer.parseInt(matcher.group(5))
                    : matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
            String meridiem = matcher.group(3);
            if (meridiem != null) {
                int normalizedHour = hour % 12 + (meridiem.equalsIgnoreCase("pm") ? 12 : 0);
                result = LocalTime.of(normalizedHour, minute);
            } else {
                result = LocalTime.of(hour, minute);
            }
        }
        return result;
    }
}
