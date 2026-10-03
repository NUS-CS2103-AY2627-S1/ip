package ramly.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import ramly.validation.DateTimeParser;

/** Represents a task that must be completed by a date and optional time. */
public class Deadline extends Task {
    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter.ofPattern("h:mma");
    private final LocalDateTime by;


    /** Creates a deadline from a supported user-facing date format. */
    public Deadline(String description, String by) {
        this(description, DateTimeParser.parseDeadlineInput(by));
    }

    /** Creates a deadline from its parsed date and time. */
    private Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /** Creates a deadline from a canonical or legacy stored value. */
    public static Deadline fromStorage(String description, String by) {
        return new Deadline(description, DateTimeParser.parseStoredDeadline(by));
    }

    /** Returns the parsed deadline date and time. */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns whether another deadline has the same description and deadline. */
    @Override
    public boolean hasSameIdentity(Task other) {
        return other instanceof Deadline
                && super.hasSameIdentity(other)
                && by.equals(((Deadline) other).by);
    }

    /** Formats the deadline for display to the user. */
    private String displayDateTime() {
        String date = by.format(DISPLAY_DATE_FORMAT);
        return by.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? date
                : date + ", " + by.format(DISPLAY_TIME_FORMAT);
    }

    /** Returns the serialized deadline representation. */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + by.format(STORAGE_FORMAT);
    }

    /** Returns the user-facing deadline representation. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + displayDateTime() + ")";
    }
}
