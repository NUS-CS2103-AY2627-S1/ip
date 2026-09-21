package margit.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Represents a task date or date-time while preserving unparseable user input. */
public class TaskDateTime {
    /** Format used when displaying a date to the user. */
    public static final DateTimeFormatter OUTPUT_DATE = DateTimeFormatter.ofPattern("MMM d yyyy");

    private static final String DATE_TIME_PREFIX = "DT:";
    private static final String DATE_PREFIX = "D:";
    private static final String RAW_PREFIX = "RAW:";
    private static final LocalTime DATE_ONLY_SORT_TIME = LocalTime.of(23, 59);

    private static final DateTimeFormatter[] INPUT_DATETIME_FORMATS = {
        DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
        DateTimeFormatter.ofPattern("d/M/yyyy H:mm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("MMM d yyyy HHmm"),
    };
    private static final DateTimeFormatter[] INPUT_DATE_FORMATS = {
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
    };
    private static final DateTimeFormatter OUTPUT_DATETIME = DateTimeFormatter.ofPattern("MMM d yyyy, h:mma");
    private static final DateTimeFormatter SAVE_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter SAVE_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final LocalDateTime dateTime;
    private final boolean hasTime;
    private final String raw;

    private TaskDateTime(LocalDateTime dateTime, boolean hasTime, String raw) {
        this.dateTime = dateTime;
        this.hasTime = hasTime;
        this.raw = raw;
    }

    /** Parses a user-supplied date or date-time. */
    public static TaskDateTime parse(String input) {
        input = input.trim();
        for (DateTimeFormatter format : INPUT_DATETIME_FORMATS) {
            try {
                return new TaskDateTime(LocalDateTime.parse(input, format), true, null);
            } catch (DateTimeParseException ignored) {
                // Try the next accepted format.
            }
        }
        for (DateTimeFormatter format : INPUT_DATE_FORMATS) {
            try {
                return new TaskDateTime(LocalDate.parse(input, format).atStartOfDay(), false, null);
            } catch (DateTimeParseException ignored) {
                // Try the next accepted format.
            }
        }
        return new TaskDateTime(null, false, input);
    }

    /** Parses a date-only input, returning {@code null} when it is invalid. */
    public static LocalDate parseDateOnly(String input) {
        input = input.trim();
        for (DateTimeFormatter format : INPUT_DATE_FORMATS) {
            try {
                return LocalDate.parse(input, format);
            } catch (DateTimeParseException ignored) {
                // Try the next accepted format.
            }
        }
        TaskDateTime parsed = parse(input);
        return parsed.dateTime == null ? null : parsed.dateTime.toLocalDate();
    }

    /** Recreates a task date or date-time from its save-file representation. */
    public static TaskDateTime fromSaveFormat(String saved) {
        if (saved.startsWith(DATE_TIME_PREFIX)) {
            String dateTimeText = saved.substring(DATE_TIME_PREFIX.length());
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeText, SAVE_DATETIME);
            return new TaskDateTime(dateTime, true, null);
        }
        if (saved.startsWith(DATE_PREFIX)) {
            return new TaskDateTime(LocalDate.parse(saved.substring(DATE_PREFIX.length()), SAVE_DATE).atStartOfDay(),
                    false, null);
        }
        if (saved.startsWith(RAW_PREFIX)) {
            return new TaskDateTime(null, false, saved.substring(RAW_PREFIX.length()));
        }
        return new TaskDateTime(null, false, saved);
    }

    /** Returns the representation used when saving this value. */
    public String toSaveFormat() {
        if (dateTime == null) {
            return RAW_PREFIX + raw;
        }
        return hasTime ? DATE_TIME_PREFIX + dateTime.format(SAVE_DATETIME)
                : DATE_PREFIX + dateTime.format(SAVE_DATE);
    }

    /** Returns this value's date component, or {@code null} for unparseable input. */
    public LocalDate toLocalDate() {
        return dateTime == null ? null : dateTime.toLocalDate();
    }

    /** Returns the chronological value used for sorting, or {@code null} for unparseable input. */
    public LocalDateTime toSortDateTime() {
        if (dateTime == null) {
            return null;
        }
        return hasTime ? dateTime : dateTime.toLocalDate().atTime(DATE_ONLY_SORT_TIME);
    }

    /** Returns this value in the user-facing date or date-time format. */
    @Override
    public String toString() {
        if (dateTime == null) {
            return raw;
        }
        return hasTime ? dateTime.format(OUTPUT_DATETIME) : dateTime.format(OUTPUT_DATE);
    }
}
