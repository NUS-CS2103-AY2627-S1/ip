import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Parses the date and time formats accepted by Furina. */
class DateTimeParser {
    private static final DateTimeFormatter ISO_DATE = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd")
            .optionalStart()
            .appendPattern(" HHmm")
            .optionalEnd()
            .optionalStart()
            .appendPattern(" HH:mm")
            .optionalEnd()
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter DAY_MONTH_DATE = new DateTimeFormatterBuilder()
            .appendPattern("d/M/uuuu")
            .optionalStart()
            .appendPattern(" HHmm")
            .optionalEnd()
            .optionalStart()
            .appendPattern(" HH:mm")
            .optionalEnd()
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses an ISO or day/month date, optionally followed by a time.
     *
     * @param text date text such as {@code 2019-10-15} or {@code 2/12/2019 1800}
     * @return the parsed date and time, using midnight when no time is supplied
     * @throws DateTimeParseException if the text is not a valid supported date
     */
    static LocalDateTime parse(String text) {
        String trimmed = text.trim();
        try {
            return LocalDateTime.parse(trimmed, ISO_DATE);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(trimmed, DAY_MONTH_DATE);
            } catch (DateTimeParseException ignoredTime) {
                try {
                    return java.time.LocalDate.parse(trimmed, ISO_DATE).atStartOfDay();
                } catch (DateTimeParseException ignoredIsoDate) {
                    return java.time.LocalDate.parse(trimmed, DAY_MONTH_DATE).atStartOfDay();
                }
            }
        }
    }

    /** Returns the user-facing representation of a parsed date and time. */
    static String format(LocalDateTime dateTime) {
        Locale english = Locale.ENGLISH;
        String date = dateTime.format(DateTimeFormatter.ofPattern("MMM d uuuu", english));
        if (dateTime.toLocalTime().equals(java.time.LocalTime.MIDNIGHT)) {
            return date;
        }
        return date + " " + dateTime.format(DateTimeFormatter.ofPattern("h:mm a", english));
    }
}
