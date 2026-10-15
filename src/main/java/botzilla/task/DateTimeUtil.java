package botzilla.task;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import botzilla.BotzillaException;

/**
 * Utility for parsing user-entered dates/times in "d/M/yyyy" or
 * "d/M/yyyy HHmm" format, and formatting them for display or for
 * saving to disk.
 */
public class DateTimeUtil {
    private static final DateTimeFormatter OUTPUT_WITH_TIME =
            DateTimeFormatter.ofPattern("dd MMM yyyy, h:mma");
    private static final DateTimeFormatter OUTPUT_DATE_ONLY =
            DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter FILE_FORMAT_WITH_TIME =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter FILE_FORMAT_DATE_ONLY =
            DateTimeFormatter.ofPattern("d/M/yyyy");

    // Recognises text shaped like "d/M/yyyy" or "d/M/yyyy <time>". Matched
    // manually (rather than via DateTimeFormatter) so that day/month/time
    // values are validated with LocalDate.of/LocalTime.of below: java.time's
    // default (SMART) resolver silently clamps an out-of-range day-of-month
    // to the last valid day of the month (e.g. "30/2/2019" would otherwise
    // become 28 Feb 2019 with no warning), which would hide typos instead
    // of reporting them.
    private static final Pattern DATE_SHAPE = Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{4})(?:\\s+(\\d+))?");

    /**
     * Attempts to parse the given text as a date or date-time in
     * "d/M/yyyy" or "d/M/yyyy HHmm" shape. Text that isn't shaped like a
     * date at all (e.g. free-form text such as "next Friday") is not an
     * error: this returns empty so the caller can store it as-is.
     *
     * @param text the raw user input to parse
     * @return the parsed LocalDateTime, or empty if the text isn't date-shaped
     * @throws BotzillaException if the text is date-shaped but names an
     *                           invalid calendar date or time (e.g. "30/2/2019",
     *                           a month outside 1-12, or a time outside HHmm range)
     */
    public static Optional<LocalDateTime> parse(String text) throws BotzillaException {
        String trimmed = text.trim();
        Matcher matcher = DATE_SHAPE.matcher(trimmed);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        int day = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int year = Integer.parseInt(matcher.group(3));
        LocalDate date;
        try {
            date = LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            throw new BotzillaException("\"" + trimmed + "\" isn't a real calendar date "
                    + "(check the day and month) — please use d/M/yyyy, e.g. 2/12/2019.");
        }

        String timePart = matcher.group(4);
        if (timePart == null) {
            return Optional.of(date.atStartOfDay());
        }
        if (timePart.length() != 4) {
            throw new BotzillaException("\"" + timePart + "\" isn't a valid time — please use 4 digits "
                    + "in HHmm format, e.g. 1800 for 6pm.");
        }
        int hour = Integer.parseInt(timePart.substring(0, 2));
        int minute = Integer.parseInt(timePart.substring(2, 4));
        try {
            return Optional.of(LocalDateTime.of(date, LocalTime.of(hour, minute)));
        } catch (DateTimeException e) {
            throw new BotzillaException("\"" + timePart + "\" isn't a valid time — please use 4 digits "
                    + "in HHmm format, e.g. 1800 for 6pm.");
        }
    }

    /**
     * Checks whether the given text (assumed already parseable by
     * {@link #parse}) includes a time component, as opposed to a
     * date-only value.
     *
     * @param text the raw user input to check
     * @return true if the text includes a time component
     */
    public static boolean hasTimeComponent(String text) {
        Matcher matcher = DATE_SHAPE.matcher(text.trim());
        return matcher.matches() && matcher.group(4) != null;
    }

    /**
     * Formats a date-time for display to the user, e.g.
     * "Dec 02 2019, 6:00PM" if hasTime is true, or "Dec 02 2019" if false.
     *
     * @param dateTime the date-time to format
     * @param hasTime  whether a time component should be included
     * @return the formatted display string
     */
    public static String formatForDisplay(LocalDateTime dateTime, boolean hasTime) {
        return hasTime ? dateTime.format(OUTPUT_WITH_TIME) : dateTime.format(OUTPUT_DATE_ONLY);
    }

    /**
     * Formats a date-time for saving to disk, matching the same
     * "d/M/yyyy [HHmm]" format accepted by {@link #parse}.
     *
     * @param dateTime the date-time to format
     * @param hasTime  whether a time component should be included
     * @return the formatted file string
     */
    public static String formatForFile(LocalDateTime dateTime, boolean hasTime) {
        return hasTime ? dateTime.format(FILE_FORMAT_WITH_TIME) : dateTime.format(FILE_FORMAT_DATE_ONLY);
    }
}
