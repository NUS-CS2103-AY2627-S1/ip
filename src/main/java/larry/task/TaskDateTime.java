package larry.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents date and time information attached to a task.
 * Parses task dates from Larry's command format and presents them in a
 * user-friendly format.
 */
public final class TaskDateTime {
    private static final String CURRENT_DATE_PROPERTY = "larry.currentDate";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d-M-uuuu", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d MMM uuuu, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String sourceText;
    private final Optional<LocalDateTime> value;

    /**
     * Creates a date-time value from Larry's command format.
     *
     * Dates use day-month-year order and can appear before or after the time.
     * An omitted year means the current year, while a time without a date means
     * today.
     *
     * @param input Date and time text accepted by Larry.
     * @throws DateTimeParseException If the input is not a valid date and time.
     */
    public TaskDateTime(String input) {
        Objects.requireNonNull(input, "input");
        LocalDateTime parsedValue = parseInput(input, getCurrentDate());
        this.sourceText = input;
        this.value = Optional.of(parsedValue);
    }

    /**
     * Creates a date-time value backed by a parsed {@link LocalDateTime}.
     *
     * @param value Parsed date and time.
     */
    public TaskDateTime(LocalDateTime value) {
        LocalDateTime nonNullValue = Objects.requireNonNull(value, "value");
        this.sourceText = nonNullValue.format(STORAGE_FORMATTER);
        this.value = Optional.of(nonNullValue);
    }

    /**
     * Creates a task date and time with a specified parsed state.
     *
     * @param sourceText Original saved text.
     * @param value Parsed value, if the saved text uses the current format.
     */
    private TaskDateTime(String sourceText, Optional<LocalDateTime> value) {
        this.sourceText = sourceText;
        this.value = value;
    }

    /**
     * Creates a date-time value from saved text.
     * Current ISO values are parsed, while legacy free-form values are retained
     * so that tasks saved by an earlier Larry version are not lost.
     *
     * @param storedValue Saved date and time text.
     * @return Parsed task date and time, or a legacy display-only value.
     */
    public static TaskDateTime fromStorageString(String storedValue) {
        Objects.requireNonNull(storedValue, "storedValue");
        try {
            return new TaskDateTime(LocalDateTime.parse(storedValue, STORAGE_FORMATTER));
        } catch (DateTimeParseException e) {
            return new TaskDateTime(storedValue, Optional.empty());
        }
    }

    /**
     * Returns the parsed date and time when the value uses the current format.
     *
     * @return Parsed date and time, or an empty value for legacy saved text.
     */
    public Optional<LocalDateTime> getValue() {
        return value;
    }

    /**
     * Parses a date using Singapore day-month-year order.
     * An omitted year means the current year.
     *
     * @param input Date text using hyphens or slashes.
     * @return Parsed date.
     * @throws DateTimeParseException If the input is not a supported valid date.
     */
    public static LocalDate parseDate(String input) {
        Objects.requireNonNull(input, "input");
        return parseDateWithCurrentYear(input.trim(), getCurrentDate(), input);
    }

    /**
     * Formats a date using Singapore day-month-year order.
     *
     * @param date Date to format.
     * @return Date in a format such as {@code 2 Dec 2019}.
     */
    public static String formatDate(LocalDate date) {
        return Objects.requireNonNull(date, "date").format(DISPLAY_DATE_FORMATTER);
    }

    /**
     * Checks whether this value occurs on a date.
     *
     * @param date Date to check.
     * @return True when the parsed value falls on the date.
     */
    public boolean isOn(LocalDate date) {
        Objects.requireNonNull(date, "date");
        return value.map(dateTime -> dateTime.toLocalDate().equals(date))
                .orElse(false);
    }

    /**
     * Returns today's date, allowing automated tests to provide a fixed date.
     *
     * @return Current local date or the configured test date.
     */
    private static LocalDate getCurrentDate() {
        String configuredDate = System.getProperty(CURRENT_DATE_PROPERTY);
        if (configuredDate == null) {
            return LocalDate.now();
        }
        return LocalDate.parse(configuredDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Parses supported combinations of date and time using one reference date.
     *
     * @param input Date and time text accepted by Larry.
     * @param currentDate Date used when the year or full date is omitted.
     * @return Parsed date and time.
     * @throws DateTimeParseException If the input is not a supported valid value.
     */
    private static LocalDateTime parseInput(String input, LocalDate currentDate) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length == 1) {
            return LocalDateTime.of(currentDate, parseTime(parts[0], input));
        }
        if (parts.length != 2) {
            throw invalidInput(input);
        }

        if (isDate(parts[0]) && isTime(parts[1])) {
            return LocalDateTime.of(parseDateWithCurrentYear(parts[0], currentDate, input),
                    parseTime(parts[1], input));
        }
        if (isTime(parts[0]) && isDate(parts[1])) {
            return LocalDateTime.of(parseDateWithCurrentYear(parts[1], currentDate, input),
                    parseTime(parts[0], input));
        }
        throw invalidInput(input);
    }

    /**
     * Parses a Singapore-style date, filling in an omitted year.
     *
     * @param dateText Date text using hyphens or slashes.
     * @param currentDate Date supplying the current year.
     * @param fullInput Full input used in parse errors.
     * @return Parsed date.
     */
    private static LocalDate parseDateWithCurrentYear(String dateText, LocalDate currentDate,
            String fullInput) {
        String normalizedDate = dateText.replace('/', '-');
        if (dateText.matches("\\d{1,2}[-/]\\d{1,2}")) {
            normalizedDate += "-" + currentDate.getYear();
        } else if (!dateText.matches("\\d{1,2}[-/]\\d{1,2}[-/]\\d{4}")) {
            throw invalidInput(fullInput);
        }

        try {
            return LocalDate.parse(normalizedDate, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw invalidInput(fullInput);
        }
    }

    /**
     * Parses a 24-hour time with or without a colon.
     *
     * @param timeText Time text to parse.
     * @param fullInput Full input used in parse errors.
     * @return Parsed time.
     */
    private static LocalTime parseTime(String timeText, String fullInput) {
        String normalizedTime;
        if (timeText.matches("\\d{1,2}:\\d{2}")) {
            normalizedTime = timeText.replace(":", "");
        } else if (timeText.matches("\\d{3,4}")) {
            normalizedTime = timeText;
        } else {
            throw invalidInput(fullInput);
        }
        if (normalizedTime.length() == 3) {
            normalizedTime = "0" + normalizedTime;
        }

        try {
            return LocalTime.parse(normalizedTime, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw invalidInput(fullInput);
        }
    }

    /**
     * Checks whether a token has the shape of a supported date.
     *
     * @param value Token to inspect.
     * @return True when the token contains a date separator.
     */
    private static boolean isDate(String value) {
        return value.contains("-") || value.contains("/");
    }

    /**
     * Checks whether a token has the shape of a supported time.
     *
     * @param value Token to inspect.
     * @return True when the token contains only digits and an optional colon.
     */
    private static boolean isTime(String value) {
        return value.matches("\\d{3,4}") || value.matches("\\d{1,2}:\\d{2}");
    }

    /**
     * Creates a consistent parse failure for unsupported input.
     *
     * @param input Invalid date and time text.
     * @return Parse exception describing the invalid input.
     */
    private static DateTimeParseException invalidInput(String input) {
        return new DateTimeParseException("Unsupported date and time", input, 0);
    }

    /**
     * Returns the stable representation used in the task data file.
     *
     * @return Date and time text suitable for storage.
     */
    public String toStorageString() {
        return value.map(dateTime -> dateTime.format(STORAGE_FORMATTER))
                .orElse(sourceText);
    }

    /**
     * Returns a user-friendly date and time.
     *
     * @return Date and time in a format such as {@code 2 Dec 2019, 6:00 PM}.
     */
    @Override
    public String toString() {
        return value.map(dateTime -> dateTime.format(DISPLAY_DATE_TIME_FORMATTER))
                .orElse(sourceText);
    }
}
