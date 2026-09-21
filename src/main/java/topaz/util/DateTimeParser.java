package topaz.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Pattern;

import topaz.TopazException;

/** Parses the date and date-time formats supported by Topaz. */
public final class DateTimeParser {
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern ISO_DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    private DateTimeParser() {
    }

    /**
     * Parses a supported date or date-time value.
     *
     * @param text the date or date-time text to parse
     * @param errorMessage the error message to show when no supported format matches
     * @return the parsed date and time, with midnight used for date-only values
     * @throws TopazException if the text is not a supported date or date-time format
     */
    public static LocalDateTime parse(String text, String errorMessage) throws TopazException {
        try {
            return LocalDateTime.parse(text, DATE_TIME_INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            // Try the supported ISO date-only format next.
        }

        try {
            return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException exception) {
            // Try the supported ISO date-time format next.
        }

        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException exception) {
            throw new TopazException(errorMessage);
        }
    }

    /**
     * Rejects events with an end at or before their start.
     *
     * @param from the parsed event start
     * @param to the parsed event end
     * @throws TopazException if the event has zero or negative duration
     */
    public static void validateEventPeriod(LocalDateTime from, LocalDateTime to) throws TopazException {
        if (!to.isAfter(from)) {
            throw new TopazException("The event end must be after its start.");
        }
    }

    /**
     * Returns whether the supplied date text includes a time component.
     *
     * @param text the date text to inspect
     * @return true if the text is not an ISO date-only value
     */
    public static boolean hasTimeComponent(String text) {
        return !ISO_DATE_PATTERN.matcher(text).matches();
    }
}
