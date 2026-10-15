package clue.tasks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/** Task base class. Contains some constants useful for its children, and keeps track of name and completion status. */
public abstract class Task {
    public static final DateTimeFormatter IN_FORMAT = new DateTimeFormatterBuilder()
            .optionalStart()
            .appendPattern("yyyy-")
            .optionalEnd()
            .appendPattern("MM-dd")
            .optionalStart()
            .appendPattern(" HH:mm")
            .optionalEnd()
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
            .toFormatter();
    public static final DateTimeFormatter OUT_FORMAT = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM);
    public static final DateTimeFormatter OUT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd/MM/yy");


    protected final String name;
    private boolean isDone;

    /** Constructs a task with the given name, and a default completion state of false. */
    public Task(String name) {
        this.name = validateName(name);
        this.isDone = false;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.isDone ? "X" : " ", this.name);
    }

    /** Serializes of this task to a string. Used to reconstruct the task using {@link construct}. */
    public abstract String save(String separator);

    /** Recreates the task using parts returned by {@link save}. */
    public static Task construct(String... args) {
        return null;
    }

    static String displayDate(LocalDateTime date) {
        if (date.toLocalTime().equals(LocalTime.MIN)) {
            return date.format(OUT_DATE_FORMAT);
        }
        return date.format(OUT_FORMAT);
    }

    /** The default date used by this class and its subclasses. */
    public static LocalDateTime defaultDate() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
    }

    static LocalDateTime tryParseDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return defaultDate();
        }
        try {
            return LocalDateTime.parse(date);
        } catch (DateTimeParseException e) {
            return defaultDate();
        }
    }

    /** Gets this task's name */
    public String getName() {
        return this.name;
    }

    /**
     * Updates the done status of this task.
     * @param isDone - what to set the done status of this task to
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Task && this.name.equals(((Task) obj).name);
    }

    /**
     * Validates that the given date is not null and is a valid date.
     *
     * @param date the date to validate
     * @param fieldName the name of the field being validated (for error messages)
     * @return the validated date
     * @throws InvalidTaskException if the date is null or invalid
     */
    static LocalDateTime validateDate(LocalDateTime date, String fieldName) {
        if (date == null) {
            throw new InvalidTaskException(fieldName + " cannot be null");
        }
        return date;
    }

    /**
     * Validates that the task name is not empty or just whitespace.
     *
     * @param name the name to validate
     * @return the validated name
     * @throws InvalidTaskException if the name is empty or blank
     */
    static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidTaskException("Task name cannot be empty");
        }
        return name.trim();
    }

    /**
     * Custom exception for task validation errors.
     */
    public static class InvalidTaskException extends RuntimeException {
        public InvalidTaskException(String message) {
            super(message);
        }
    }
}
