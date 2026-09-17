package yuki.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Optional;

/** Parses and formats all dates and times used by Yuki. */
public final class DateTimeParser {
    /** The date-time format accepted in commands, such as {@code 2/12/2019 1800}. */
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("d/M/uuuu HH")
            .optionalStart()
            .appendLiteral(':')
            .optionalEnd()
            .appendPattern("mm")
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
    /** The date-only format accepted in commands, such as {@code 12/8/2026}. */
    private static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    /** The friendly date-only format shown in Yuki's replies. */
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    /** The friendly date-time format shown in Yuki's replies. */
    private static final DateTimeFormatter DATE_TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy HH:mm", Locale.ENGLISH);

    /** Prevents creation of this stateless utility class. */
    private DateTimeParser() {
    }

    /**
     * Parses a date-time.
     *
     * @param text Date or date-time entered by the user.
     * @return A parsed date-time, or the unchanged text if no date format matches.
     */
    public static TaskDateTime parse(String text) {
        return tryParseDateTime(text)
                .map(TaskDateTime::of)
                .orElseGet(() -> parseDateOrText(text));
    }

    /** Returns a parsed date, or preserves the original text when no date format matches. */
    private static TaskDateTime parseDateOrText(String text) {
        return tryParseDate(text, DATE_INPUT_FORMAT)
                .or(() -> tryParseDate(text, DateTimeFormatter.ISO_LOCAL_DATE))
                .map(TaskDateTime::of)
                .orElseGet(() -> TaskDateTime.of(text));
    }

    /** Attempts to parse text using Yuki's supported date-time format. */
    private static Optional<LocalDateTime> tryParseDateTime(String text) {
        try {
            return Optional.of(LocalDateTime.parse(text, DATE_TIME_INPUT_FORMAT));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /** Attempts to parse text using the supplied date format. */
    private static Optional<LocalDate> tryParseDate(String text, DateTimeFormatter format) {
        try {
            return Optional.of(LocalDate.parse(text, format));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /** Parses the ISO date-time format used in the save file. */
    public static TaskDateTime parseStored(String text) {
        if (text.startsWith("DT:")) {
            return TaskDateTime.of(LocalDateTime.parse(text.substring(3)));
        }
        if (text.startsWith("D:")) {
            return TaskDateTime.of(LocalDate.parse(text.substring(2)));
        }
        if (text.startsWith("T:")) {
            // Read time-only values written by the previous implementation.
            return TaskDateTime.of(LocalTime.parse(text.substring(2))
                    .format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)));
        }
        if (text.startsWith("S:")) {
            return TaskDateTime.of(text.substring(2));
        }

        try {
            return TaskDateTime.of(LocalDateTime.parse(text));
        } catch (DateTimeParseException e) {
            try {
                return TaskDateTime.of(LocalDate.parse(text));
            } catch (DateTimeParseException ignored) {
                // Also load arbitrary strings written by earlier versions.
                return parse(text);
            }
        }
    }

    /** Returns a date-time in Yuki's friendly display format. */
    public static String format(TaskDateTime value) {
        if (value.hasDateTime()) {
            return value.getDateTime().format(DATE_TIME_DISPLAY_FORMAT);
        }
        if (value.hasDateOnly()) {
            return value.getDate().format(DATE_DISPLAY_FORMAT);
        }
        return value.getText();
    }

    /** Returns an unambiguous value for the save file. */
    public static String formatStored(TaskDateTime value) {
        if (value.hasDateTime()) {
            return "DT:" + value.getDateTime();
        }
        if (value.hasDateOnly()) {
            return "D:" + value.getDate();
        }
        return "S:" + value.getText();
    }
}
