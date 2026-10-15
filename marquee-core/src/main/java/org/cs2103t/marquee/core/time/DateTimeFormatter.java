package org.cs2103t.marquee.core.time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Custom {@code LocalDateTime} formatter and parser that supports many date-time formats used in daily life.
 * <h1>
 * Absolute time format
 * <p>
 * This format consist of a date component and a time component.
 * At least 1 of date or time component below must exist to be considered a valid date-time string.
 * <p>
 * If time component exist but date component is missing,
 * it is assumed to be on the current day, taken from {@link LocalDate#now()}.
 * <p>
 * If date component exist but time component is missing,
 * it is assumed to be midnight at the start of that day (00:00:00).
 * <h3>
 * Available date component formats
 * <ul>
 *     <li>{@code today}</li>
 *     <li>{@code yesterday} and {@code tomorrow}, can be repeated for further future and past days</li>
 *     <li>{@code last} day-of-week, {@code last} can be repeated for further past days-of-week</li>
 *     <li>{@code next} day-of-week, {@code next} can be repeated for further future days-of-week</li>
 *     <li>Day, month, year, separated by forward-slash {@code /} or hyphen {@code -}</li>
 *     <li>Day, written month, year, separated by 1 or more spaces <code>&nbsp;</code>,
 *     comma {@code ,} with optional trailing spaces, or hyphen {@code -}</li>
 *     <li>Written month, day, year, same separator as above except for spaces</li>
 * </ul>
 * Separator choices must be consistent throughout the date component
 * <p>
 * Day-of-week and written month can be full or abbreviated.
 * <p>
 * Year component can be omitted, then it is assumed to be the current year
 * taken from {@link LocalDateTime#now()}{@link LocalDateTime#getYear() .getYear()}.
 * <h3>
 * Available time component formats
 * <ul>
 *     <li>Hour, minute, second, separated by semicolon {@code :}</li>
 *     <li>Hour, minute, separator optional</li>
 * </ul>
 * Hour, minute and seconds must be 2 digits.
 * <p>
 * Second component can be omitted, then it is assumed to be 00.
 * <h1>
 * Relative time format
 * <p>
 * Strings following this format must end in {@code later} or {@code ago} to indicate relative time.
 * <p>
 * This format consist of any amount of duration component separated by spaces.
 * Duration components can have duplicate units, in which case they are added up.
 * <p>
 * Negative durations are not allowed. Use {@code ago} instead to indicate time in the past.
 * <h3>
 * Available duration component formats
 * <ul>
 *     <li>number, optional spaces <code>&nbsp;</code>, duration abbreviation</li>
 *     <li>number, 1 or more spaces <code>&nbsp;</code> and duration full name</li>
 * </ul>
 * <h3>
 * Available duration components and abbreviations
 * <ul>
 *     <li>{@code second}/{@code seconds} component: {@code s}, {@code sec}</li>
 *     <li>{@code minute}/{@code minutes} component: {@code m}, {@code min}</li>
 *     <li>{@code hour}/{@code hours} component: {@code h}, {@code hr}, {@code hrs}</li>
 *     <li>{@code day}/{@code days} component: {@code d}</li>
 * </ul>
 * The final date-time is the current time truncated to the shortest unit used in the string,
 * then shifted by the duration specified in the string
 *
 * @see LocalDateTime
 */
public final class DateTimeFormatter {
    /*** Full names for the days-of-week. */
    public static final List<String> DAYS_OF_WEEK = List.of(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    );

    /*** Full names for the months. */
    public static final List<String> MONTHS = List.of(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    );

    private static final String RELATIVE_TIME_FUTURE_SUFFIX = "later";
    private static final String RELATIVE_TIME_PAST_SUFFIX = "ago";

    private static final List<String> DAYS_OF_WEEK_PREFIX = DAYS_OF_WEEK.stream()
            .map(dow -> dow.substring(0, 3).toLowerCase()).toList();
    private static final List<String> MONTHS_PREFIX = MONTHS.stream()
            .map(month -> month.substring(0, 3).toLowerCase()).toList();

    private static final Pattern TIME_FULL_PATTERN = Pattern.compile(
            "\\G\\s*(?<hour>\\d{2}):(?<minute>\\d{2})(?::(?<second>\\d{2}))?(?:$|\\s+)"
    );
    private static final Pattern TIME_ABBREV_PATTERN = Pattern.compile(
            "\\G\\s*(?<hour>\\d{2})(?<minute>\\d{2})(?:$|\\s+)"
    );
    private static final Pattern TODAY_PATTERN = Pattern.compile(
            "\\G\\s*today(?:$|\\s+)"
    );
    private static final Pattern YESTERDAY_PATTERN = Pattern.compile(
            "\\G\\s*yesterday(?<yesterday>(?: yesterday)*)(?:$|\\s+)"
    );
    private static final Pattern TOMORROW_PATTERN = Pattern.compile(
            "\\G\\s*tomorrow(?<tomorrow>(?: tomorrow)*)(?:$|\\s+)"
    );
    private static final Pattern DAYS_OF_WEEK_PATTERN = Pattern.compile(
            "\\G\\s*(?:(?<last>last(?: last)*)|(?<next>next(?: next)*)) (?<dayOfWeek>"
                    + DAYS_OF_WEEK.stream()
                    .map(dow ->
                            "[" + dow.charAt(0) + Character.toLowerCase(dow.charAt(0)) + "]"
                                    + dow.substring(1, 3)
                                    + "(?:" + dow.substring(3) + ")?"
                    )
                    .collect(Collectors.joining("|"))
                    + ")(?:$|\\s+)"
    );
    private static final Pattern DATE_TEXT_PATTERN = Pattern.compile(
            "\\G\\s*(?<day>\\d{1,2})"
                    + "(,\\s*|-|\\s+)(?<month>"
                    + MONTHS.stream()
                    .map(dow ->
                            "[" + dow.charAt(0) + Character.toLowerCase(dow.charAt(0)) + "]"
                                    + dow.substring(1, 3)
                                    + "(?:" + dow.substring(3) + ")?"
                    )
                    .collect(Collectors.joining("|"))
                    + ")"
                    + "(?:\\2\\s*(?<year>\\d{1,4}))?(?:$|\\s+)"
    );
    private static final Pattern DATE_TEXT_REVERSED_PATTERN = Pattern.compile(
            "\\G\\s*(?<month>"
                    + MONTHS.stream()
                    .map(month ->
                            "[" + month.charAt(0) + Character.toLowerCase(month.charAt(0)) + "]"
                                    + month.substring(1, 3)
                                    + "(?:" + month.substring(3) + ")?"
                    )
                    .collect(Collectors.joining("|"))
                    + ")"
                    + "(,\\s*|-)(?<day>\\d{1,2})"
                    + "(?:\\2(?<year>\\d{1,4}))?(?:$|\\s+)"
    );
    private static final Pattern DATE_NUMBER_PATTERN = Pattern.compile(
            "\\G\\s*(?<day>\\d{1,2})([/-])(?<month>\\d{1,2})(:?\\2(?<year>\\d{1,4}))?(?:$|\\s+)"
    );
    private static final Pattern SECONDS_PATTERN = Pattern.compile(
            "\\G\\s*(?<seconds>\\d+)\\s*(?:\\sseconds?|sec|s)(?:$|\\s+)"
    );
    private static final Pattern MINUTES_PATTERN = Pattern.compile(
            "\\G\\s*(?<minutes>\\d+)\\s*(?:\\sminutes?|min|m)(?:$|\\s+)"
    );
    private static final Pattern HOURS_PATTERN = Pattern.compile(
            "\\G\\s*(?<hours>\\d+)\\s*(?:\\shours?|hrs?|h)(?:$|\\s+)"
    );
    private static final Pattern DAYS_PATTERN = Pattern.compile(
            "\\G\\s*(?<days>\\d+)\\s*(?:\\sdays?|d)(?:$|\\s+)"
    );

    // prevents instantiation
    private DateTimeFormatter() {}

    /**
     * Parses the given string as a {@code LocalDateTime}, according to the class-defined format.
     *
     * @param input the string to be parsed
     * @return the parsed {@link LocalDateTime}
     * @throws DateTimeParseException if the string doesn't follow the class-defined format
     */
    public static LocalDateTime parseDateTime(String input) throws DateTimeParseException {
        if (input == null || input.isEmpty()) {
            return null;
        } else if (input.equals("now")) {
            return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        } else {
            try {
                return parseRelativeTime(input);
            } catch (DateTimeParseException e) {
                if (e.getMessage().equals("Not a relative time")) {
                    return parseTimestamp(input);
                } else {
                    throw e;
                }
            }
        }
    }

    /**
     * Returns a string representation of the given {@code LocalDateTime}, according to the class-defined format.
     *
     * @param dateTime the {@link LocalDateTime} to format
     * @return the formatted date-time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        StringBuilder res = new StringBuilder();

        LocalDate today = LocalDate.now();
        LocalDate target = dateTime.toLocalDate();

        long daysDifference = today.until(dateTime, ChronoUnit.DAYS);
        if (daysDifference == 0) {
            res.append("today");
        } else if (daysDifference == -1) {
            res.append("yesterday");
        } else if (daysDifference == 1) {
            res.append("tomorrow");
        } else if (daysDifference > -7 && daysDifference <= 7) {
            res.append(daysDifference > 0 ? "next " : "last ");
            res.append(DAYS_OF_WEEK.get(target.getDayOfWeek().getValue() - 1));
        } else if (today.getYear() == target.getYear()) {
            res.append(dateTime.getDayOfMonth()).append(' ')
                    .append(MONTHS.get(dateTime.getMonthValue() - 1));
        } else {
            res.append(dateTime.getDayOfMonth()).append(' ')
                    .append(MONTHS.get(dateTime.getMonthValue() - 1)).append(' ')
                    .append(target.getYear());
        }

        if (dateTime.getHour() != 0 || dateTime.getMinute() != 0 || dateTime.getSecond() != 0) {
            res.append(String.format(" %02d:%02d", dateTime.getHour(), dateTime.getMinute()));
            if (dateTime.getSecond() != 0) {
                res.append(String.format(":%02d", dateTime.getSecond()));
            }
        }

        return res.toString();
    }

    private static LocalDateTime parseTimestamp(String input) throws DateTimeParseException {
        Matcher timeFullMatcher = TIME_FULL_PATTERN.matcher(input);
        Matcher timeAbbrevMatcher = TIME_ABBREV_PATTERN.matcher(input);
        Matcher todayMatcher = TODAY_PATTERN.matcher(input);
        Matcher yesterdayMatcher = YESTERDAY_PATTERN.matcher(input);
        Matcher tomorrowMatcher = TOMORROW_PATTERN.matcher(input);
        Matcher dayOfWeekMatcher = DAYS_OF_WEEK_PATTERN.matcher(input);
        Matcher dateTextMatcher = DATE_TEXT_PATTERN.matcher(input);
        Matcher dateTextReversedMatcher = DATE_TEXT_REVERSED_PATTERN.matcher(input);
        Matcher dateNumberMatcher = DATE_NUMBER_PATTERN.matcher(input);

        LocalDateTime now = LocalDateTime.now();
        int index = 0;
        int second = 0;
        int minute = 0;
        int hour = 0;
        int day = now.getDayOfMonth();
        int month = now.getMonthValue();
        int year = now.getYear();
        boolean setTime = false;
        boolean setDate = false;
        while (index < input.length()) {
            if (!setTime) {
                if (timeFullMatcher.find(index)) {
                    second = timeFullMatcher.group("second") != null
                            ? Integer.parseInt(timeFullMatcher.group("second"))
                            : second;
                    minute = Integer.parseInt(timeFullMatcher.group("minute"));
                    hour = Integer.parseInt(timeFullMatcher.group("hour"));
                    index = timeFullMatcher.end();
                    setTime = true;
                    continue;
                }
                if (timeAbbrevMatcher.find(index)) {
                    minute = Integer.parseInt(timeAbbrevMatcher.group("minute"));
                    hour = Integer.parseInt(timeAbbrevMatcher.group("hour"));
                    index = timeAbbrevMatcher.end();
                    setTime = true;
                    continue;
                }
            }
            if (!setDate) {
                if (todayMatcher.find(index)) {
                    LocalDate targetDate = LocalDate.now();
                    day = targetDate.getDayOfMonth();
                    month = targetDate.getMonthValue();
                    year = targetDate.getYear();
                    index = todayMatcher.end();
                    setDate = true;
                    continue;
                }
                if (yesterdayMatcher.find(index)) {
                    int count = yesterdayMatcher.group("yesterday").length() / 10 + 1;
                    LocalDate targetDate = LocalDate.now().minusDays(count);
                    day = targetDate.getDayOfMonth();
                    month = targetDate.getMonthValue();
                    year = targetDate.getYear();
                    index = yesterdayMatcher.end();
                    setDate = true;
                    continue;
                }
                if (tomorrowMatcher.find(index)) {
                    int count = tomorrowMatcher.group("tomorrow").length() / 9 + 1;
                    LocalDate targetDate = LocalDate.now().plusDays(count);
                    day = targetDate.getDayOfMonth();
                    month = targetDate.getMonthValue();
                    year = targetDate.getYear();
                    index = tomorrowMatcher.end();
                    setDate = true;
                    continue;
                }
                if (dayOfWeekMatcher.find(index)) {
                    int dayOfWeek = DAYS_OF_WEEK_PREFIX.indexOf(dayOfWeekMatcher.group("dayOfWeek")
                            .substring(0, 3).toLowerCase()) + 1;
                    LocalDate targetDate = dayOfWeekMatcher.group("next") != null
                            ? LocalDate.now()
                            .with(TemporalAdjusters.next(DayOfWeek.of(dayOfWeek)))
                            .plusWeeks(dayOfWeekMatcher.group("next").length() / 5)
                            : LocalDate.now()
                            .with(TemporalAdjusters.previous(DayOfWeek.of(dayOfWeek)))
                            .minusWeeks(dayOfWeekMatcher.group("last").length() / 5);
                    day = targetDate.getDayOfMonth();
                    month = targetDate.getMonthValue();
                    year = targetDate.getYear();
                    index = dayOfWeekMatcher.end();
                    setDate = true;
                    continue;
                }
                if (dateTextMatcher.find(index)) {
                    day = Integer.parseInt(dateTextMatcher.group("day"));
                    month = MONTHS_PREFIX.indexOf(dateTextMatcher.group("month")
                            .substring(0, 3).toLowerCase()) + 1;
                    year = dateTextMatcher.group("year") != null
                            ? Integer.parseInt(dateTextMatcher.group("year"))
                            : year;
                    index = dateTextMatcher.end();
                    setDate = true;
                    continue;
                }
                if (dateTextReversedMatcher.find(index)) {
                    day = Integer.parseInt(dateTextReversedMatcher.group("day"));
                    month = MONTHS_PREFIX.indexOf(dateTextReversedMatcher.group("month")
                            .substring(0, 3).toLowerCase()) + 1;
                    year = dateTextReversedMatcher.group("year") != null
                            ? Integer.parseInt(dateTextReversedMatcher.group("year"))
                            : year;
                    index = dateTextReversedMatcher.end();
                    setDate = true;
                    continue;
                }
                if (dateNumberMatcher.find(index)) {
                    day = Integer.parseInt(dateNumberMatcher.group("day"));
                    month = Integer.parseInt(dateNumberMatcher.group("month"));
                    year = dateNumberMatcher.group("year") != null
                            ? Integer.parseInt(dateNumberMatcher.group("year"))
                            : year;
                    index = dateNumberMatcher.end();
                    setDate = true;
                    continue;
                }
            }
            throw new DateTimeParseException("Invalid date-time", input, index);
        }
        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    private static LocalDateTime parseRelativeTime(String input) throws DateTimeParseException {
        boolean isFuture;
        if (input.endsWith(RELATIVE_TIME_FUTURE_SUFFIX)) {
            isFuture = true;
            input = input.substring(0, input.length() - RELATIVE_TIME_FUTURE_SUFFIX.length());
        } else if (input.endsWith(RELATIVE_TIME_PAST_SUFFIX)) {
            isFuture = false;
            input = input.substring(0, input.length() - RELATIVE_TIME_PAST_SUFFIX.length());
        } else {
            throw new DateTimeParseException("Not a relative time", input, input.length());
        }

        Matcher secondsMatcher = SECONDS_PATTERN.matcher(input);
        Matcher minutesMatcher = MINUTES_PATTERN.matcher(input);
        Matcher hoursMatcher = HOURS_PATTERN.matcher(input);
        Matcher daysMatcher = DAYS_PATTERN.matcher(input);

        int index = 0;
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        boolean isSecondsSet = false;
        boolean isMinutesSet = false;
        boolean isHoursSet = false;
        while (index < input.length()) {
            if (secondsMatcher.find(index)) {
                seconds += Integer.parseInt(secondsMatcher.group("seconds"));
                isSecondsSet = true;
                index = secondsMatcher.end();
            } else if (minutesMatcher.find(index)) {
                minutes += Integer.parseInt(minutesMatcher.group("minutes"));
                isMinutesSet = true;
                index = minutesMatcher.end();
            } else if (hoursMatcher.find(index)) {
                hours += Integer.parseInt(hoursMatcher.group("hours"));
                isHoursSet = true;
                index = hoursMatcher.end();
            } else if (daysMatcher.find(index)) {
                days += Integer.parseInt(daysMatcher.group("days"));
                index = daysMatcher.end();
            } else {
                throw new DateTimeParseException("Invalid duration", input, index);
            }
        }

        LocalDateTime truncatedNow = isSecondsSet
                ? LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                : isMinutesSet
                  ? LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                  : isHoursSet
                    ? LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
                    : LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        return isFuture
                ? truncatedNow.plusSeconds(seconds).plusMinutes(minutes).plusHours(hours).plusDays(days)
                : truncatedNow.minusSeconds(seconds).minusMinutes(minutes).minusHours(hours).minusDays(days);
    }
}
