package luckynoslacky.luckyparser;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.regex.Matcher;

import luckynoslacky.luckyexception.LuckyNoInputException;
import luckynoslacky.luckyresponse.LuckyNoQuips;

/**
 * Resolves absolute and relative date expressions against a reference date.
 */
final class DateExpressionResolver {
    private DateExpressionResolver() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Finds the boundary between a date expression and its time expression.
     *
     * @param normalizedDateTimeText normalized date/time expression
     * @return index at which the time expression starts, or -1 if not found
     */
    static int findTimeBoundary(String normalizedDateTimeText) {
        for (int splitIndex = normalizedDateTimeText.length() - 1;
                splitIndex > 0; splitIndex--) {
            if (Character.isWhitespace(normalizedDateTimeText.charAt(splitIndex - 1))) {
                String timeText = normalizedDateTimeText.substring(splitIndex).trim();
                if (TimeTextParser.resolveClockTime(timeText) != null) {
                    if (isValidDateYearPair(
                            normalizedDateTimeText.substring(0, splitIndex), timeText)) {
                        continue;
                    }
                    return splitIndex;
                }
            }
        }
        return -1;
    }

    /**
     * Resolves a date expression and its relative terms against today.
     *
     * @param dateText date text to parse
     * @param today current date used for relative resolution
     * @return resolved date
     * @throws LuckyNoInputException if the date is invalid
     */
    static LocalDate resolveDateText(String dateText, LocalDate today)
            throws LuckyNoInputException {
        String normalizedDateText = DateExpressionSupport.normalizeDateText(dateText);
        Prefix prefix = extractRelativePrefix(normalizedDateText);
        String dateExpression = prefix.remainder();

        LocalDate relativeDate = tryRelativeDate(dateExpression, today, prefix);
        if (relativeDate != null) {
            return relativeDate;
        }

        String dateWithoutWeekday = DateExpressionSupport.removeWeekdayPrefix(dateExpression);
        return tryCalendarDate(dateWithoutWeekday, today, prefix);
    }

    /**
     * Checks whether a candidate date and year form a valid date.
     *
     * @param dateText date portion of the candidate
     * @param yearText year or compact-time portion of the candidate
     * @return true if the candidate can be parsed as a date
     */
    private static boolean isValidDateYearPair(String dateText, String yearText) {
        String normalizedDateText = DateExpressionSupport.removeWeekdayPrefix(
                DateExpressionSupport.normalizeDateText(dateText));
        String candidateDateText = normalizedDateText + " " + yearText;
        return DateExpressionSupport.findKnownFormat(candidateDateText) != null;
    }

    /**
     * Attempts to resolve a date expression that uses a relative keyword.
     *
     * @param dateExpression date expression after prefix extraction
     * @param today current date used for relative resolution
     * @param prefix relative-date prefix
     * @return resolved relative date, or null when the expression is absolute
     */
    private static LocalDate tryRelativeDate(
            String dateExpression, LocalDate today, Prefix prefix) {
        DayOfWeek weekday = DateExpressionSupport.findWeekday(dateExpression);
        if (weekday != null) {
            return calculateWeekdayDate(weekday, today, prefix);
        }

        if (!prefix.modifier().equals("none")) {
            return null;
        }

        return switch (dateExpression) {
            case "today" -> today;
            case "tomorrow", "tmr" -> today.plusDays(1);
            case "yesterday", "ytd" -> today.minusDays(1);
            default -> null;
        };
    }

    /**
     * Attempts to resolve a calendar date expression that is not a weekday.
     *
     * @param dateExpression date expression without a weekday prefix
     * @param today current date used for resolving incomplete dates
     * @param prefix relative-date prefix
     * @return resolved date
     * @throws LuckyNoInputException if the expression is invalid
     */
    private static LocalDate tryCalendarDate(
            String dateExpression, LocalDate today, Prefix prefix)
            throws LuckyNoInputException {
        if (dateExpression.equals("month")) {
            return resolveRelativeMonth(today, prefix);
        }
        if (dateExpression.equals("year")) {
            return resolveRelativeYear(today, prefix);
        }

        if (DateExpressionSupport.isYearOnly(dateExpression)) {
            return LocalDate.of(Integer.parseInt(dateExpression), 1, 1);
        }

        LocalDate formattedDate = DateExpressionSupport.findKnownFormat(dateExpression);
        if (formattedDate != null) {
            return formattedDate;
        }

        Matcher monthDayMatcher = DateExpressionSupport.matchMonthDay(dateExpression);
        if (monthDayMatcher.matches()) {
            return resolveMonthDay(
                    monthDayMatcher.group(1),
                    Integer.parseInt(monthDayMatcher.group(2)),
                    monthDayMatcher.group(3),
                    today,
                    prefix);
        }

        Matcher dayMonthMatcher = DateExpressionSupport.matchDayMonth(dateExpression);
        if (dayMonthMatcher.matches()) {
            return resolveMonthDay(
                    dayMonthMatcher.group(2),
                    Integer.parseInt(dayMonthMatcher.group(1)),
                    dayMonthMatcher.group(3),
                    today,
                    prefix);
        }

        Matcher dayMatcher = DateExpressionSupport.matchDayOnly(dateExpression);
        if (dayMatcher.matches()) {
            return resolveDayOfMonth(
                    Integer.parseInt(dayMatcher.group(1)), today, prefix);
        }

        int monthNumber = DateExpressionSupport.findMonthNumber(dateExpression);
        if (monthNumber > 0) {
            return resolveNamedMonth(monthNumber, today, prefix);
        }

        throw invalidDateTime();
    }

    /**
     * Calculates a calendar date from a weekday and its relative prefix.
     *
     * @param weekday requested day of the week
     * @param today current date
     * @param prefix relative-date prefix
     * @return resolved weekday date
     */
    private static LocalDate calculateWeekdayDate(
            DayOfWeek weekday, LocalDate today, Prefix prefix) {
        LocalDate currentWeekStart = today.with(DayOfWeek.MONDAY);
        int weekdayOffset = weekday.getValue() % 7;

        if (prefix.modifier().equals("this")) {
            int daysFromMonday = (weekday.getValue()
                    - DayOfWeek.MONDAY.getValue() + 7) % 7;
            return currentWeekStart.plusDays(daysFromMonday);
        }

        if (prefix.modifier().equals("next")
                || prefix.modifier().equals("following")) {
            return currentWeekStart
                    .plusDays(6)
                    .plusWeeks(prefix.count() - 1L)
                    .plusDays(weekdayOffset);
        }

        int daysUntil = (weekday.getValue() - today.getDayOfWeek().getValue() + 7) % 7;
        if (daysUntil == 0) {
            daysUntil = 7;
        }
        return today.plusDays(daysUntil);
    }

    /**
     * Resolves a relative reference to a month.
     *
     * @param today current date
     * @param prefix relative-date prefix
     * @return first day of the resolved month
     */
    private static LocalDate resolveRelativeMonth(
            LocalDate today, Prefix prefix) {
        int monthOffset = switch (prefix.modifier()) {
            case "next", "following" -> prefix.count();
            case "coming" -> 1;
            default -> 0;
        };
        return today.withDayOfMonth(1).plusMonths(monthOffset);
    }

    /**
     * Resolves a named month to its next applicable year.
     *
     * @param monthNumber month number
     * @param today current date
     * @param prefix relative-date prefix
     * @return first day of the resolved month
     */
    private static LocalDate resolveNamedMonth(
            int monthNumber, LocalDate today, Prefix prefix) {
        int yearOffset = switch (prefix.modifier()) {
            case "next", "following" -> prefix.count();
            case "coming" -> 1;
            default -> 0;
        };
        LocalDate candidateDate = LocalDate.of(today.getYear(), monthNumber, 1)
                .plusYears(yearOffset);
        if (prefix.modifier().equals("none") && candidateDate.isBefore(today)) {
            candidateDate = candidateDate.plusYears(1);
        }
        return candidateDate;
    }

    /**
     * Resolves a relative reference to a year.
     *
     * @param today current date
     * @param prefix relative-date prefix
     * @return first day of the resolved year
     */
    private static LocalDate resolveRelativeYear(
            LocalDate today, Prefix prefix) {
        int yearOffset = switch (prefix.modifier()) {
            case "next", "following" -> prefix.count();
            case "coming" -> 1;
            default -> 0;
        };
        return LocalDate.of(today.getYear() + yearOffset, 1, 1);
    }

    /**
     * Resolves a month/day expression with an optional year and prefix.
     *
     * @param monthText month name or abbreviation
     * @param dayOfMonth day of month
     * @param yearText explicit year, or null when omitted
     * @param today current date
     * @param prefix relative-date prefix
     * @return resolved date
     * @throws LuckyNoInputException if the date is invalid
     */
    private static LocalDate resolveMonthDay(
            String monthText,
            int dayOfMonth,
            String yearText,
            LocalDate today,
            Prefix prefix) throws LuckyNoInputException {
        int monthNumber = DateExpressionSupport.findMonthNumber(monthText);
        if (monthNumber < 1 || dayOfMonth < 1 || dayOfMonth > 31) {
            throw invalidDateTime();
        }

        if (yearText != null) {
            try {
                return LocalDate.of(
                        Integer.parseInt(yearText), monthNumber, dayOfMonth);
            } catch (DateTimeException exception) {
                throw invalidDateTime();
            }
        }

        LocalDate candidateDate;
        try {
            candidateDate = LocalDate.of(today.getYear(), monthNumber, dayOfMonth);
        } catch (DateTimeException exception) {
            throw invalidDateTime();
        }

        int prefixCount = prefix.count();
        boolean strictNext = prefixCount > 0;
        if ((strictNext && !candidateDate.isAfter(today))
                || (!strictNext && candidateDate.isBefore(today))) {
            candidateDate = candidateDate.plusYears(1);
        }
        if (prefixCount > 1) {
            candidateDate = candidateDate.plusYears(prefixCount - 1L);
        }
        return candidateDate;
    }

    /**
     * Resolves a day-of-month expression to its next applicable month.
     *
     * @param dayOfMonth day of month
     * @param today current date
     * @param prefix relative-date prefix
     * @return resolved date
     * @throws LuckyNoInputException if the day cannot occur in nearby months
     */
    private static LocalDate resolveDayOfMonth(
            int dayOfMonth, LocalDate today, Prefix prefix)
            throws LuckyNoInputException {
        if (dayOfMonth < 1 || dayOfMonth > 31) {
            throw invalidDateTime();
        }

        LocalDate candidateDate = findValidDay(
                today.withDayOfMonth(1), dayOfMonth);
        int prefixCount = prefix.count();
        boolean strictNext = prefixCount > 0;
        if ((strictNext && !candidateDate.isAfter(today))
                || (!strictNext && candidateDate.isBefore(today))) {
            candidateDate = findValidDay(
                    candidateDate.plusMonths(1), dayOfMonth);
        }
        for (int i = 1; i < prefixCount; i++) {
            candidateDate = findValidDay(
                    candidateDate.plusMonths(1), dayOfMonth);
        }
        return candidateDate;
    }

    /**
     * Finds the first nearby month that contains the requested day.
     *
     * @param monthStart first day of the month to try
     * @param dayOfMonth day of month
     * @return date using the requested day of the month
     * @throws LuckyNoInputException if the day is absent from the next three months
     */
    private static LocalDate findValidDay(
            LocalDate monthStart, int dayOfMonth)
            throws LuckyNoInputException {
        for (int monthOffset = 0; monthOffset < 3; monthOffset++) {
            LocalDate candidateMonth = monthStart.plusMonths(monthOffset);
            if (dayOfMonth <= candidateMonth.lengthOfMonth()) {
                return candidateMonth.withDayOfMonth(dayOfMonth);
            }
        }

        throw invalidDateTime();
    }

    /**
     * Extracts a relative-date modifier and its repetition count.
     *
     * @param normalizedDateText normalized date text
     * @return parsed prefix and remaining date expression
     */
    private static Prefix extractRelativePrefix(String normalizedDateText) {
        int count = 0;
        String remainder = normalizedDateText;

        if (remainder.startsWith("this coming ")) {
            return new Prefix("coming", 1,
                    remainder.substring("this coming ".length()).trim());
        }
        if (remainder.startsWith("coming ")) {
            return new Prefix("coming", 1,
                    remainder.substring("coming ".length()).trim());
        }
        if (remainder.startsWith("this ")) {
            return new Prefix("this", 0,
                    remainder.substring("this ".length()).trim());
        }
        while (remainder.startsWith("next ")) {
            count++;
            remainder = remainder.substring("next ".length()).trim();
        }
        if (remainder.startsWith("following ")) {
            count = Math.max(2, count + 1);
            remainder = remainder.substring("following ".length()).trim();
            return new Prefix("following", count, remainder);
        }
        return new Prefix(count == 0 ? "none" : "next", count, remainder);
    }

    /**
     * Creates the standard exception for invalid date/time input.
     *
     * @return invalid date/time exception
     */
    private static LuckyNoInputException invalidDateTime() {
        return new LuckyNoInputException(LuckyNoQuips.invalidDateTimeMessage());
    }

    /** Stores a relative-date modifier, count, and remaining expression. */
    private record Prefix(String modifier, int count, String remainder) {
    }
}
