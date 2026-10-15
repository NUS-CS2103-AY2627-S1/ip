package niulai.model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents either a recognized temporal value or preserved free-form text.
 */
public final class TemporalValue {
    private static final Pattern ISO_DATE_PATTERN =
            Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern SLASH_DATE_PATTERN =
            Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}$");
    private static final Pattern NUMERIC_DATE_TIME_PATTERN = Pattern.compile(
            "^((?:\\d{4}-\\d{2}-\\d{2})|(?:\\d{1,2}/\\d{1,2}/\\d{4}))"
                    + "\\s+(\\d{4}|\\d{1,2}:\\d{2})$"
    );
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "^(?:\\d{4}|\\d{1,2}:\\d{2}|\\d{1,2}(?::\\d{2})?(?i:am|pm))$"
    );
    private static final Pattern MONTH_DAY_PATTERN = Pattern.compile(
            "^(?i:(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|"
                    + "jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|"
                    + "dec(?:ember)?))\\s+(\\d{1,2})(st|nd|rd|th)?(?:\\s+(\\d{4}))?"
                    + "(?:\\s+(.+))?$"
    );
    private static final DateTimeFormatter ISO_DATE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter SLASH_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    private final Kind kind;
    private final String text;
    private final LocalDate date;
    private final LocalDateTime dateTime;
    private final MonthDay monthDay;
    private final LocalTime time;

    private TemporalValue(Kind kind, String text, LocalDate date,
                          LocalDateTime dateTime, MonthDay monthDay, LocalTime time) {
        this.kind = kind;
        this.text = text;
        this.date = date;
        this.dateTime = dateTime;
        this.monthDay = monthDay;
        this.time = time;
    }

    /**
     * Parses a supported temporal shape or preserves an unrecognized free-form value.
     *
     * @param input the temporal text to classify
     * @return the classified value
     * @throws IllegalArgumentException if the value is blank or resembles an invalid date or time
     */
    public static TemporalValue parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("A date or time cannot be blank.");
        }

        String value = input.strip();
        Matcher dateTimeMatcher = NUMERIC_DATE_TIME_PATTERN.matcher(value);
        if (dateTimeMatcher.matches()) {
            LocalDate parsedDate = parseNumericDate(dateTimeMatcher.group(1));
            LocalTime parsedTime = parseTime(dateTimeMatcher.group(2));
            return ofDateTime(value, LocalDateTime.of(parsedDate, parsedTime));
        }
        if (ISO_DATE_PATTERN.matcher(value).matches()
                || SLASH_DATE_PATTERN.matcher(value).matches()) {
            return ofDate(value, parseNumericDate(value));
        }
        if (TIME_PATTERN.matcher(value).matches()) {
            return ofTime(value, parseTime(value));
        }

        Matcher monthDayMatcher = MONTH_DAY_PATTERN.matcher(value);
        if (monthDayMatcher.matches()) {
            return parseMonthDay(value, monthDayMatcher);
        }
        return freeForm(value);
    }

    /**
     * Returns the full date contained in this value.
     *
     * @return the date, or {@code null} if no full date is available
     */
    public LocalDate getDate() {
        if (date != null) {
            return date;
        }
        return dateTime == null ? null : dateTime.toLocalDate();
    }

    /**
     * Checks that an event end is later than its start whenever the values are comparable.
     *
     * @param start the event start value
     * @param end the event end value
     * @return true if the values are incomparable or the end is strictly later
     */
    public static boolean isEndStrictlyAfter(TemporalValue start, TemporalValue end) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");

        if (start.kind == Kind.DATE && end.kind == Kind.DATE) {
            return end.date.isAfter(start.date);
        }
        if (start.kind == Kind.DATE_TIME && end.kind == Kind.DATE_TIME) {
            return end.dateTime.isAfter(start.dateTime);
        }
        if (start.kind == Kind.MONTH_DAY && end.kind == Kind.MONTH_DAY) {
            return end.monthDay.compareTo(start.monthDay) > 0;
        }
        if (start.kind == Kind.MONTH_DAY_TIME && end.kind == Kind.MONTH_DAY_TIME) {
            int dateComparison = end.monthDay.compareTo(start.monthDay);
            return dateComparison > 0
                    || dateComparison == 0 && end.time.isAfter(start.time);
        }
        if (start.kind == Kind.TIME && end.kind == Kind.TIME) {
            return end.time.isAfter(start.time);
        }
        if (start.kind == Kind.DATE_TIME && end.kind == Kind.TIME) {
            return LocalDateTime.of(start.dateTime.toLocalDate(), end.time)
                    .isAfter(start.dateTime);
        }
        if (start.kind == Kind.MONTH_DAY_TIME && end.kind == Kind.TIME) {
            return end.time.isAfter(start.time);
        }
        return true;
    }

    @Override
    public String toString() {
        return text;
    }

    private static TemporalValue parseMonthDay(String value, Matcher matcher) {
        Month month = parseMonth(matcher.group(1));
        int day = Integer.parseInt(matcher.group(2));
        validateOrdinal(day, matcher.group(3));

        MonthDay parsedMonthDay;
        try {
            parsedMonthDay = MonthDay.of(month, day);
        } catch (DateTimeException e) {
            throw invalidValue(value, e);
        }

        String yearText = matcher.group(4);
        String timeText = matcher.group(5);
        if (yearText != null) {
            LocalDate parsedDate;
            try {
                parsedDate = LocalDate.of(Integer.parseInt(yearText), month, day);
            } catch (DateTimeException e) {
                throw invalidValue(value, e);
            }
            if (timeText == null) {
                return ofDate(value, parsedDate);
            }
            if (TIME_PATTERN.matcher(timeText).matches()) {
                return ofDateTime(value, LocalDateTime.of(parsedDate, parseTime(timeText)));
            }
            return freeForm(value);
        }

        if (timeText == null) {
            return ofMonthDay(value, parsedMonthDay);
        }
        if (TIME_PATTERN.matcher(timeText).matches()) {
            return ofMonthDayTime(value, parsedMonthDay, parseTime(timeText));
        }
        return freeForm(value);
    }

    private static LocalDate parseNumericDate(String value) {
        DateTimeFormatter formatter = value.contains("/")
                ? SLASH_DATE_FORMATTER : ISO_DATE_FORMATTER;
        try {
            return LocalDate.parse(value, formatter);
        } catch (DateTimeParseException e) {
            throw invalidValue(value, e);
        }
    }

    private static LocalTime parseTime(String value) {
        try {
            String lowerCaseValue = value.toLowerCase(Locale.ROOT);
            if (lowerCaseValue.endsWith("am") || lowerCaseValue.endsWith("pm")) {
                boolean isAfternoon = lowerCaseValue.endsWith("pm");
                String digits = lowerCaseValue.substring(0, lowerCaseValue.length() - 2);
                String[] fields = digits.split(":", -1);
                int hour = Integer.parseInt(fields[0]);
                int minute = fields.length == 1 ? 0 : Integer.parseInt(fields[1]);
                if (hour < 1 || hour > 12) {
                    throw new DateTimeException("Hour is outside the 12-hour clock.");
                }
                int adjustedHour = hour % 12 + (isAfternoon ? 12 : 0);
                return LocalTime.of(adjustedHour, minute);
            }
            if (value.contains(":")) {
                String[] fields = value.split(":", -1);
                return LocalTime.of(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]));
            }
            return LocalTime.of(
                    Integer.parseInt(value.substring(0, 2)),
                    Integer.parseInt(value.substring(2))
            );
        } catch (DateTimeException | NumberFormatException e) {
            throw invalidValue(value, e);
        }
    }

    private static Month parseMonth(String value) {
        return switch (value.substring(0, 3).toLowerCase(Locale.ROOT)) {
            case "jan" -> Month.JANUARY;
            case "feb" -> Month.FEBRUARY;
            case "mar" -> Month.MARCH;
            case "apr" -> Month.APRIL;
            case "may" -> Month.MAY;
            case "jun" -> Month.JUNE;
            case "jul" -> Month.JULY;
            case "aug" -> Month.AUGUST;
            case "sep" -> Month.SEPTEMBER;
            case "oct" -> Month.OCTOBER;
            case "nov" -> Month.NOVEMBER;
            case "dec" -> Month.DECEMBER;
            default -> throw new IllegalArgumentException("Unsupported month: " + value);
        };
    }

    private static void validateOrdinal(int day, String suffix) {
        if (suffix == null) {
            return;
        }
        int lastTwoDigits = day % 100;
        String expectedSuffix;
        if (lastTwoDigits >= 11 && lastTwoDigits <= 13) {
            expectedSuffix = "th";
        } else {
            expectedSuffix = switch (day % 10) {
                case 1 -> "st";
                case 2 -> "nd";
                case 3 -> "rd";
                default -> "th";
            };
        }
        if (!expectedSuffix.equalsIgnoreCase(suffix)) {
            throw invalidValue(day + suffix, null);
        }
    }

    private static IllegalArgumentException invalidValue(String value, Exception cause) {
        return new IllegalArgumentException("Invalid date or time: " + value + ".", cause);
    }

    private static TemporalValue freeForm(String value) {
        return new TemporalValue(Kind.FREE_FORM, value, null, null, null, null);
    }

    private static TemporalValue ofDate(String value, LocalDate date) {
        return new TemporalValue(Kind.DATE, value, date, null, null, null);
    }

    private static TemporalValue ofDateTime(String value, LocalDateTime dateTime) {
        return new TemporalValue(Kind.DATE_TIME, value, null, dateTime, null, null);
    }

    private static TemporalValue ofMonthDay(String value, MonthDay monthDay) {
        return new TemporalValue(Kind.MONTH_DAY, value, null, null, monthDay, null);
    }

    private static TemporalValue ofMonthDayTime(
            String value, MonthDay monthDay, LocalTime time) {
        return new TemporalValue(Kind.MONTH_DAY_TIME, value, null, null, monthDay, time);
    }

    private static TemporalValue ofTime(String value, LocalTime time) {
        return new TemporalValue(Kind.TIME, value, null, null, null, time);
    }

    private enum Kind {
        FREE_FORM,
        DATE,
        DATE_TIME,
        MONTH_DAY,
        MONTH_DAY_TIME,
        TIME
    }
}
