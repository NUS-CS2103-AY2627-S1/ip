package luckynoslacky.luckyparser;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import luckynoslacky.luckyexception.LuckyNoInputException;
import luckynoslacky.luckyresponse.LuckyNoQuips;

/**
 * Parses the flexible date and time text accepted by the chatbot.
 *
 * <p>This class coordinates input normalization, clock-time parsing, date
 * resolution, and storage conversion while preserving the public parser API.
 * The individual concerns are implemented by package-level collaborators.</p>
 */
public final class DateTimeParser {
    private final Clock clock;

    /** Creates a parser using the system's default time zone and clock. */
    public DateTimeParser() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a parser using a supplied clock, which is useful for deterministic
     * tests.
     *
     * @param clock clock used to resolve relative dates
     */
    public DateTimeParser(Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("Clock cannot be null.");
        }
        this.clock = clock;
    }

    /**
     * Represents a parsed date/time together with whether its date was omitted.
     *
     * @param dateTime parsed date and time
     * @param timeOnly true when the input contained no date
     */
    public record ParsedDateTime(LocalDateTime dateTime, boolean timeOnly) {
    }

    /**
     * Parses date/time text used as an event start.
     *
     * @param dateTimeText date and time text to parse
     * @return parsed date and time
     * @throws LuckyNoInputException if the input is not a supported date/time
     */
    public ParsedDateTime parseStartDateTime(String dateTimeText)
            throws LuckyNoInputException {
        return parse(dateTimeText, LocalTime.MIN);
    }

    /**
     * Parses date/time text used as an event end or deadline.
     *
     * @param dateTimeText date and time text to parse
     * @return parsed date and time
     * @throws LuckyNoInputException if the input is not a supported date/time
     */
    public ParsedDateTime parseEndDateTime(String dateTimeText)
            throws LuckyNoInputException {
        return parse(dateTimeText, LocalTime.of(23, 59));
    }

    /**
     * Parses an event end time relative to its start date and time.
     *
     * <p>An explicitly supplied end date is preserved. When the end input
     * contains only a time, the reference date is used unless that time has
     * already passed, in which case the next date is used.</p>
     *
     * @param dateTimeText event end date/time text
     * @param referenceDateTime event start date and time
     * @return parsed event end date and time
     * @throws LuckyNoInputException if the input is invalid
     */
    public ParsedDateTime parseEndDateTime(
            String dateTimeText,
            LocalDateTime referenceDateTime)
            throws LuckyNoInputException {
        if (referenceDateTime == null) {
            throw new IllegalArgumentException(
                    "Reference date and time cannot be null.");
        }

        ParsedDateTime parsed = parse(dateTimeText, LocalTime.of(23, 59));
        if (!parsed.timeOnly()) {
            return parsed;
        }

        LocalTime endTime = parsed.dateTime().toLocalTime();
        LocalDate endDate = referenceDateTime.toLocalDate();
        if (endTime.isBefore(referenceDateTime.toLocalTime())) {
            endDate = endDate.plusDays(1);
        }

        return new ParsedDateTime(
                LocalDateTime.of(endDate, endTime),
                true);
    }

    /**
     * Returns the current local date and time for this parser's clock.
     *
     * @return current local date and time
     */
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    /**
     * Parses date/time text and applies the default time used when only a date
     * is given.
     *
     * @param dateTimeText date/time text to parse
     * @param dateOnlyDefault time to use for date-only input
     * @return parsed date and time
     * @throws LuckyNoInputException if the input is invalid
     */
    private ParsedDateTime parse(String dateTimeText, LocalTime dateOnlyDefault)
            throws LuckyNoInputException {
        String normalizedDateTimeText = normalizeDateTimeText(dateTimeText);
        if (normalizedDateTimeText.isEmpty()) {
            throw invalidDateTime();
        }

        LocalDateTime currentDateTime = now();
        LocalDate today = currentDateTime.toLocalDate();

        if (DateExpressionSupport.isYearOnly(normalizedDateTimeText)) {
            LocalDate date = DateExpressionResolver.resolveDateText(
                    normalizedDateTimeText, today);
            return new ParsedDateTime(LocalDateTime.of(date, dateOnlyDefault), false);
        }

        ParsedDateTime timeOnly = parseTimeOnly(
                normalizedDateTimeText, currentDateTime, today);
        if (timeOnly != null) {
            return timeOnly;
        }

        ParsedDateTime dateAndTime = parseDateTime(
                normalizedDateTimeText, today);
        if (dateAndTime != null) {
            return dateAndTime;
        }

        LocalDate date = DateExpressionResolver.resolveDateText(
                normalizedDateTimeText, today);
        return new ParsedDateTime(LocalDateTime.of(date, dateOnlyDefault), false);
    }

    /**
     * Parses an input containing only a time and resolves it to today or tomorrow.
     *
     * @param normalizedDateTimeText normalized input text
     * @param currentDateTime current date and time
     * @param today current date
     * @return parsed time-only input, or null when the input is not time-only
     */
    private ParsedDateTime parseTimeOnly(
            String normalizedDateTimeText,
            LocalDateTime currentDateTime,
            LocalDate today) {
        LocalTime parsedTime = TimeTextParser.resolveClockTime(normalizedDateTimeText);
        if (parsedTime == null) {
            return null;
        }

        LocalDate resolvedDate = parsedTime.isBefore(currentDateTime.toLocalTime())
                ? today.plusDays(1)
                : today;
        return new ParsedDateTime(LocalDateTime.of(resolvedDate, parsedTime), true);
    }

    /**
     * Parses an input containing both a date and a time.
     *
     * @param normalizedDateTimeText normalized date/time text
     * @param today current date used for resolving incomplete dates
     * @return parsed date/time input, or null when no date/time split is found
     * @throws LuckyNoInputException if the date portion is invalid
     */
    private ParsedDateTime parseDateTime(
            String normalizedDateTimeText, LocalDate today)
            throws LuckyNoInputException {
        int dateTimeSplitIndex = DateExpressionResolver.findTimeBoundary(
                normalizedDateTimeText);
        if (dateTimeSplitIndex <= 0) {
            return null;
        }

        String dateText = normalizedDateTimeText
                .substring(0, dateTimeSplitIndex).trim();
        String timeText = normalizedDateTimeText
                .substring(dateTimeSplitIndex).trim();
        LocalTime parsedTime = TimeTextParser.resolveClockTime(timeText);
        if (parsedTime == null) {
            return null;
        }

        LocalDate date = DateExpressionResolver.resolveDateText(dateText, today);
        return new ParsedDateTime(LocalDateTime.of(date, parsedTime), false);
    }

    /**
     * Normalizes general date/time input before parsing.
     *
     * @param rawDateTimeText raw date/time text
     * @return trimmed text with equivalent separators and spacing normalized
     */
    private static String normalizeDateTimeText(String rawDateTimeText) {
        if (rawDateTimeText == null) {
            return "";
        }
        return rawDateTimeText.trim()
                .replaceAll("(?<=\\d)T(?=\\d)", " ")
                .replaceAll("(?i)a\\.m\\.", "am")
                .replaceAll("(?i)p\\.m\\.", "pm")
                .replaceAll(",", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Creates the standard exception for invalid date/time input.
     *
     * @return invalid date/time exception
     */
    private static LuckyNoInputException invalidDateTime() {
        return new LuckyNoInputException(LuckyNoQuips.invalidDateTimeMessage());
    }
}
