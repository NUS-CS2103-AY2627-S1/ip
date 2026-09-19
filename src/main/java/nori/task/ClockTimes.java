package nori.task;

import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the clock time a user wrote inside free-form task details.
 */
final class ClockTimes {
    /** Hours in half a day, which is what separates a morning hour from its afternoon reading. */
    private static final int HOURS_IN_HALF_DAY = 12;
    /**
     * Matches the first clock time in a detail, in any of the forms Nori accepts.
     *
     * The three alternatives are a separated time such as {@code 9:30}, a
     * compact 24-hour time such as {@code 0930}, and a whole hour that a
     * meridiem gives meaning to, such as {@code 2pm}. Digits either side of a
     * match rule it out, so a longer run of digits is never read as a time.
     */
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "(?<![0-9])(?:"
                    + "(?<hour>[01]?[0-9]|2[0-3])[:.](?<minute>[0-5][0-9])\\s*(?<meridiem>[ap]\\.?m\\.?)?"
                    + "|(?<compactHour>[01][0-9]|2[0-3])(?<compactMinute>[0-5][0-9])"
                    + "|(?<wholeHour>1[0-2]|0?[1-9])\\s*(?<wholeMeridiem>[ap]\\.?m\\.?)"
                    + ")(?![0-9])",
            Pattern.CASE_INSENSITIVE);
    /**
     * Matches text written in the shape of a clock time, whether or not a clock could show it.
     *
     * The two shapes are a colon-separated time such as {@code 25:00} and an
     * hour followed by a meridiem such as {@code 13pm}. A dotted pair is left
     * out because {@code 9.75} may be a price, not a time.
     */
    private static final Pattern TIME_SHAPE_PATTERN = Pattern.compile(
            "(?<![0-9:.])(?:"
                    + "(?<hour>[0-9]{1,2}):(?<minute>[0-9]{2})"
                    + "|(?<meridiemHour>[0-9]{1,2})\\s*[ap]\\.?m\\.?"
                    + ")(?![0-9])",
            Pattern.CASE_INSENSITIVE);
    /** Matches details that are one compact 24-hour time and nothing else, such as {@code 2500}. */
    private static final Pattern COMPACT_TIME_SHAPE_PATTERN = Pattern.compile("[0-9]{4}");
    /** Last hour of a day on a 24-hour clock. */
    private static final int LAST_HOUR_OF_DAY = 23;
    /** Last minute of an hour. */
    private static final int LAST_MINUTE_OF_HOUR = 59;

    /** Prevents instantiation of this stateless reader. */
    private ClockTimes() {
    }

    /**
     * Returns the first clock time written in some task details.
     *
     * Every ISO-8601 date must already have been removed from {@code details},
     * because a year's four digits would otherwise read as a 24-hour time.
     *
     * A time may be written as {@code 0930}, {@code 9:30}, {@code 9.30},
     * {@code 2pm}, {@code 2:30 PM} or {@code 2 p.m.}. A meridiem is ignored
     * after an hour past noon, where it cannot say anything the hour has not
     * said already.
     *
     * @param details the task details to read, with every date removed.
     * @return the first time the details give, or empty when they give none.
     */
    static Optional<LocalTime> findFirstIn(String details) {
        Matcher matcher = TIME_PATTERN.matcher(details);
        if (!matcher.find()) {
            return Optional.empty();
        }

        if (matcher.group("compactHour") != null) {
            return Optional.of(LocalTime.of(Integer.parseInt(matcher.group("compactHour")),
                    Integer.parseInt(matcher.group("compactMinute"))));
        }
        if (matcher.group("wholeHour") != null) {
            int hourOfDay = toHourOfDay(Integer.parseInt(matcher.group("wholeHour")),
                    matcher.group("wholeMeridiem"));
            return Optional.of(LocalTime.of(hourOfDay, 0));
        }
        int hourOfDay = toHourOfDay(Integer.parseInt(matcher.group("hour")), matcher.group("meridiem"));
        return Optional.of(LocalTime.of(hourOfDay, Integer.parseInt(matcher.group("minute"))));
    }

    /**
     * Returns whether some task details say a clock time and nothing else.
     *
     * Details that carry anything besides one time may name a day in words
     * Nori does not read, such as {@code Mon 2pm}. A caller comparing two
     * times uses this to tell the details it can place on one day from the
     * details it cannot.
     *
     * @param details the task details to read, with every date removed.
     * @return {@code true} when the details are exactly one clock time.
     */
    static boolean isOnlyClockTime(String details) {
        String trimmedDetails = details.trim();
        if (trimmedDetails.isEmpty()) {
            return false;
        }

        Matcher matcher = TIME_PATTERN.matcher(trimmedDetails);
        return matcher.find() && matcher.start() == 0 && matcher.end() == trimmedDetails.length();
    }

    /**
     * Returns the first text in some task details that looks like a clock time but is not one.
     *
     * {@link #findFirstIn} passes over such text, so without this check a
     * detail such as {@code 2026-10-01 25:00} would be kept as an event with
     * no time at all. A lone four-digit detail such as {@code 2500} counts,
     * because that is the form Nori's own examples use; four digits among
     * other words may be a room number and are left alone.
     *
     * @param details the task details to read, with every date removed.
     * @return the impossible time as the user wrote it, or empty when there is none.
     */
    static Optional<String> findImpossibleTime(String details) {
        String trimmedDetails = details.strip();
        boolean isCompactTimeShape = COMPACT_TIME_SHAPE_PATTERN.matcher(trimmedDetails).matches();
        if (isCompactTimeShape && !isOnlyClockTime(trimmedDetails)) {
            return Optional.of(trimmedDetails);
        }

        Matcher matcher = TIME_SHAPE_PATTERN.matcher(details);
        while (matcher.find()) {
            if (!isRealTime(matcher)) {
                return Optional.of(matcher.group().strip());
            }
        }
        return Optional.empty();
    }

    /**
     * Returns whether a match of {@link #TIME_SHAPE_PATTERN} names a time a clock can show.
     *
     * @param matcher a matcher positioned on a match of that pattern.
     * @return {@code true} when the hour and minute are both in range.
     */
    private static boolean isRealTime(Matcher matcher) {
        if (matcher.group("hour") != null) {
            return Integer.parseInt(matcher.group("hour")) <= LAST_HOUR_OF_DAY
                    && Integer.parseInt(matcher.group("minute")) <= LAST_MINUTE_OF_HOUR;
        }
        int meridiemHour = Integer.parseInt(matcher.group("meridiemHour"));
        return meridiemHour >= 1 && meridiemHour <= HOURS_IN_HALF_DAY;
    }

    /**
     * Converts an hour as a user wrote it into an hour of the day.
     *
     * @param hour the hour as written.
     * @param meridiem the am or pm that followed it, or {@code null} when none did.
     * @return the hour of the day, from 0 to 23.
     */
    private static int toHourOfDay(int hour, String meridiem) {
        if (meridiem == null || hour > HOURS_IN_HALF_DAY) {
            return hour;
        }

        boolean isMorning = meridiem.toLowerCase(Locale.ROOT).startsWith("a");
        if (isMorning) {
            return hour == HOURS_IN_HALF_DAY ? 0 : hour;
        }
        return hour == HOURS_IN_HALF_DAY ? HOURS_IN_HALF_DAY : hour + HOURS_IN_HALF_DAY;
    }
}
