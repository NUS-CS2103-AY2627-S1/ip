package luckynoslacky.luckyparser;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the clock-time forms accepted by the date/time parser.
 */
final class TimeTextParser {
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "^(\\d{1,2})(?:(:|\\.)(\\d{2}))?(?::(\\d{2}))?\\s*(am|pm)?$",
            Pattern.CASE_INSENSITIVE);

    private TimeTextParser() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Resolves a clock expression in separated, meridiem, or compact HHMM
     * notation.
     *
     * @param timeText time text to parse
     * @return parsed time, or null if the text is not a valid time
     */
    static LocalTime resolveClockTime(String timeText) {
        String normalizedTimeText = timeText.trim()
                .replaceAll("(?i)a\\.m\\.", "am")
                .replaceAll("(?i)p\\.m\\.", "pm");
        Matcher matcher = TIME_PATTERN.matcher(normalizedTimeText);
        if (!matcher.matches()) {
            return readCompactTime(normalizedTimeText);
        }

        int hour = Integer.parseInt(matcher.group(1));
        String minuteText = matcher.group(3);
        String secondText = matcher.group(4);
        String meridiem = matcher.group(5);

        if (minuteText == null && meridiem == null) {
            return null;
        }

        int minute = minuteText == null ? 0 : Integer.parseInt(minuteText);
        int second = secondText == null ? 0 : Integer.parseInt(secondText);
        if (meridiem != null) {
            if (hour < 1 || hour > 12) {
                return null;
            }
            if (meridiem.equalsIgnoreCase("pm") && hour < 12) {
                hour += 12;
            } else if (meridiem.equalsIgnoreCase("am") && hour == 12) {
                hour = 0;
            }
        } else if (hour > 23) {
            return null;
        }

        try {
            return LocalTime.of(hour, minute, second);
        } catch (DateTimeException exception) {
            return null;
        }
    }

    /**
     * Reads a four-digit time in HHMM notation.
     *
     * @param compactTimeText compact time text
     * @return parsed time, or null if the text is not a valid HHMM value
     */
    private static LocalTime readCompactTime(String compactTimeText) {
        if (!compactTimeText.matches("\\d{4}")) {
            return null;
        }

        int hour = Integer.parseInt(compactTimeText.substring(0, 2));
        int minute = Integer.parseInt(compactTimeText.substring(2, 4));
        if (hour > 23 || minute > 59) {
            return null;
        }

        return LocalTime.of(hour, minute);
    }
}
