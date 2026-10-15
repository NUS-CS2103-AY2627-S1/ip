package samantha.model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import samantha.exception.InputException;

/**
 * Represents the value after a deadline's {@code /by} marker.
 *
 * <p>Dates are stored as typed date and time values. A missing time represents
 * an all-day deadline.</p>
 */
public final class DateTimeValue {
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "^(\\d{1,2})([/-])(\\d{1,2})\\2(\\d{4})(?:\\s+(\\d{4}))?$");
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter STORAGE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HHmm", Locale.ENGLISH);

    private final LocalDate date;
    private final Optional<LocalTime> time;

    /**
     * Creates a parsed date value with an optional time component.
     *
     * @param date parsed date
     * @param time parsed time, if supplied
     */
    private DateTimeValue(LocalDate date, Optional<LocalTime> time) {
        assert date != null : "A date-time value must contain a date";
        assert time != null : "A date-time value must contain an optional time";
        this.date = date;
        this.time = time;
    }

    /**
     * Parses a deadline value in {@code d/M/yyyy} or {@code d-M-yyyy} format,
     * with an optional {@code HHmm} time.
     *
     * @param text raw text after {@code /by}
     * @return a parsed deadline value
     * @throws InputException if the value is empty or does not follow the
     *         supported date format
     */
    public static DateTimeValue parse(String text) throws InputException {
        String value = text.trim();
        if (value.isEmpty()) {
            throw new InputException("You did not mention deadline after /by");
        }

        Matcher matcher = DATE_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw invalidFormat();
        }

        try {
            int day = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(3));
            int year = Integer.parseInt(matcher.group(4));
            LocalDate date = LocalDate.of(year, month, day);

            Optional<LocalTime> time = Optional.empty();
            if (matcher.group(5) != null) {
                String timeText = matcher.group(5);
                int hour = Integer.parseInt(timeText.substring(0, 2));
                int minute = Integer.parseInt(timeText.substring(2, 4));
                time = Optional.of(LocalTime.of(hour, minute));
            }
            return new DateTimeValue(date, time);
        } catch (DateTimeException | NumberFormatException e) {
            throw invalidFormat();
        }
    }

    /**
     * Parses a date-only value in the supported date formats.
     *
     * @param text raw date text
     * @return the parsed date
     * @throws InputException if the value is invalid or includes a time
     */
    public static LocalDate parseDate(String text) throws InputException {
        DateTimeValue value = parse(text);
        if (value.getTime().isPresent()) {
            throw invalidFormat();
        }
        assert value.getTime().isEmpty() : "A date-only value must not contain a time";
        return value.date;
    }

    /**
     * Creates the shared validation exception for unsupported date input.
     *
     * @return an exception describing the required format
     */
    private static InputException invalidFormat() {
        return new InputException(
                "The date and time must use d/M/yyyy or d-M-yyyy, optionally followed by HHmm.");
    }

    /**
     * Returns the value in a user-friendly display format.
     *
     * @return a formatted deadline
     */
    @Override
    public String toString() {
        String formattedDate = date.format(DISPLAY_DATE_FORMAT);
        return time.map(value -> formattedDate + ", "
                + value.format(DISPLAY_TIME_FORMAT))
                .orElse(formattedDate);
    }

    /**
     * Returns the value in the stable format used in saved task records.
     *
     * @return a storage-compatible date and optional time value
     */
    public String toStorageString() {
        String storedDate = date.format(STORAGE_DATE_FORMAT);
        return time.map(value -> storedDate + " "
                + value.format(STORAGE_TIME_FORMAT))
                .orElse(storedDate);
    }

    /**
     * Returns the parsed date.
     *
     * @return the parsed date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the parsed time, if one was supplied.
     *
     * @return the optional time
     */
    public Optional<LocalTime> getTime() {
        return time;
    }
}
