package luckynoslacky.luckystorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Converts task date/time values to and from the CSV storage representation.
 */
final class DateTimeStorageCodec {
    private static final DateTimeFormatter CSV_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("uuuu-MM-dd HH:mm")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    private DateTimeStorageCodec() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Formats a date/time for the CSV storage format.
     *
     * @param dateTime date and time to format, or null for an empty field
     * @return formatted storage text
     */
    static String format(LocalDateTime dateTime) {
        return dateTime == null ? "" : CSV_FORMATTER.format(dateTime);
    }

    /**
     * Parses stored date/time text from the CSV storage format.
     *
     * @param storedDateTimeText stored date and time, or blank for no date/time
     * @return parsed date and time, or null for blank stored text
     */
    static LocalDateTime parse(String storedDateTimeText) {
        return storedDateTimeText == null || storedDateTimeText.isBlank()
                ? null
                : LocalDateTime.parse(storedDateTimeText, CSV_FORMATTER);
    }
}
