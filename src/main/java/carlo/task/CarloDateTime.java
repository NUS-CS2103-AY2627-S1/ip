package carlo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a date, optionally paired with a time of day, that a user
 * typed in as part of a deadline or event.
 *
 * <p>Recognised text -- the relative keywords {@code today},
 * {@code tomorrow}, and {@code yesterday}, or an explicit format such as
 * {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} -- is parsed into a
 * {@link LocalDateTime} so it can be displayed consistently and compared
 * against other dates. Text that does not match any recognised format is
 * kept as free-form text via {@link #isParsed()} returning {@code false},
 * so callers that require an actual date (such as {@link Deadline} and
 * {@link Event}) can detect and reject it.
 */
public final class CarloDateTime {
    /**
     * A human-readable summary of the accepted date/time formats, for use
     * in validation error messages.
     */
    public static final String FORMAT_HELP =
            "Try yyyy-mm-dd, yyyy-mm-dd HHmm, or d/m/yyyy (or today/tomorrow/yesterday)!";

    private static final DateTimeFormatter[] DATE_TIME_INPUT_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("yyyy-M-d HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
    };
    private static final DateTimeFormatter[] DATE_ONLY_INPUT_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy-M-d"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
    };
    private static final DateTimeFormatter DATE_OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter DATE_TIME_OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mma");
    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FILE_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final String rawText;
    private final LocalDateTime dateTime;
    private final boolean hasTime;

    private CarloDateTime(String rawText, LocalDateTime dateTime, boolean hasTime) {
        this.rawText = rawText;
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /**
     * Parses user-entered text into a date/time. Recognises the relative
     * keywords {@code today}, {@code tomorrow}, and {@code yesterday}
     * (case-insensitive), as well as {@code yyyy-MM-dd},
     * {@code yyyy-MM-dd HHmm}, and {@code d/M/yyyy}, with or without a
     * trailing {@code HHmm} time. Text that does not match any of these is
     * kept as free-form text instead of causing an error.
     *
     * <p>This same method is used both for text a user types in and for
     * text previously written to disk, since the on-disk format is one of
     * the recognised formats above.
     *
     * @param text the text to parse
     * @return the parsed date/time, or free-form text if it could not be parsed
     */
    public static CarloDateTime parse(String text) {
        LocalDate relativeDate = resolveRelativeKeyword(text.trim());
        if (relativeDate != null) {
            return new CarloDateTime(text, relativeDate.atStartOfDay(), false);
        }

        for (DateTimeFormatter format : DATE_TIME_INPUT_FORMATS) {
            try {
                CarloDateTime result = new CarloDateTime(
                        text, LocalDateTime.parse(text, format), true);
                assert result.isParsed() : "Date-time input must be parsed";
                return result;
            } catch (DateTimeParseException ignored) {
                // try the next format
            }
        }

        for (DateTimeFormatter format : DATE_ONLY_INPUT_FORMATS) {
            try {
                LocalDate date = LocalDate.parse(text, format);
                CarloDateTime result = new CarloDateTime(
                        text, date.atStartOfDay(), false);
                assert result.isParsed() : "Date input must be parsed";
                return result;
            } catch (DateTimeParseException ignored) {
                // try the next format
            }
        }

        return new CarloDateTime(text, null, false);
    }

    /**
     * Parses text that should be a plain calendar date, such as the
     * argument to an "on {date}" command. Also recognises the relative
     * keywords {@code today}, {@code tomorrow}, and {@code yesterday}
     * (case-insensitive).
     *
     * @param text the text to parse
     * @return the parsed date
     * @throws DateTimeParseException if the text does not match any recognised date format
     */
    public static LocalDate parseDate(String text) {
        String trimmed = text.trim();

        LocalDate relativeDate = resolveRelativeKeyword(trimmed);
        if (relativeDate != null) {
            return relativeDate;
        }

        for (DateTimeFormatter format : DATE_ONLY_INPUT_FORMATS) {
            try {
                return LocalDate.parse(trimmed, format);
            } catch (DateTimeParseException ignored) {
                // try the next format
            }
        }

        throw new DateTimeParseException("Unrecognised date format: " + text, text, 0);
    }

    /**
     * Resolves the relative keywords {@code today}, {@code tomorrow}, and
     * {@code yesterday} (case-insensitive) to an actual calendar date.
     *
     * @param trimmedText the already-trimmed text to check
     * @return the resolved date, or {@code null} if {@code trimmedText} is
     *         not one of the recognised keywords
     */
    private static LocalDate resolveRelativeKeyword(String trimmedText) {
        switch (trimmedText.toLowerCase()) {
            case "today":
                return LocalDate.now();
            case "tomorrow":
                return LocalDate.now().plusDays(1);
            case "yesterday":
                return LocalDate.now().minusDays(1);
            default:
                return null;
        }
    }

    /**
     * Returns whether this value was successfully parsed into an actual date.
     *
     * @return {@code true} if this holds a real date/time, {@code false} if it is free-form text
     */
    public boolean isParsed() {
        return dateTime != null;
    }

    /**
     * Returns the display text for this date/time: {@code MMM d yyyy} if
     * only a date was given, or {@code MMM d yyyy, h:mma} if a time was
     * given too. Falls back to the original text if it could not be parsed.
     *
     * @return the display text
     */
    public String toDisplayString() {
        if (dateTime == null) {
            return rawText;
        }
        return hasTime ? dateTime.format(DATE_TIME_OUTPUT_FORMAT) : dateTime.format(DATE_OUTPUT_FORMAT);
    }

    /**
     * Returns the text used to save this date/time to disk, in a format
     * that {@link #parse(String)} can read back unambiguously. Falls back
     * to the original text if it could not be parsed.
     *
     * @return the on-disk save text
     */
    public String toFileFormat() {
        if (dateTime == null) {
            return rawText;
        }
        return hasTime ? dateTime.format(FILE_DATE_TIME_FORMAT) : dateTime.format(FILE_DATE_FORMAT);
    }

    /**
     * Returns the underlying parsed date/time.
     *
     * @return the parsed date/time, or {@code null} if this holds free-form text
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Returns whether the user gave a time of day as well as a date.
     *
     * @return {@code true} if a time of day was given, {@code false} for a date only
     */
    public boolean hasTime() {
        return hasTime;
    }

    /**
     * Returns whether this value was parsed and falls on the given calendar date.
     *
     * @param date the date to compare against
     * @return {@code true} if this value was parsed and its date matches {@code date}
     */
    public boolean isOnDate(LocalDate date) {
        return dateTime != null && dateTime.toLocalDate().equals(date);
    }

    /**
     * Returns the display text for this date/time.
     *
     * @return the display text, same as {@link #toDisplayString()}
     */
    @Override
    public String toString() {
        return toDisplayString();
    }
}
