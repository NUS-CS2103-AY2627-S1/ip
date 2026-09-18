package epi.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

/** Represents a task that must be completed by a date and time. */
public class Deadline extends Task {
    private static final DateTimeFormatter INPUT_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HHmm").parseDefaulting(ChronoField.ERA, 1).toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");
    /** Date and time by which this task should be completed. */
    protected LocalDateTime by;

    /**
     * Creates an incomplete deadline by strictly parsing its due date and time.
     *
     * @param description Task description, preserved without trimming.
     * @param byString Non-null due date/time in {@code yyyy-MM-dd HHmm} format.
     * @throws DateTimeParseException If the due date/time has an invalid format or value.
     * @throws IllegalArgumentException If the description is null, blank, or contains
     *     a pipe character, a newline, or a carriage return.
     */
    public Deadline(String description, String byString) throws DateTimeParseException {
        super(description);
        this.by = LocalDateTime.parse(byString, INPUT_FORMAT);
    }

    /**
     * Copies a deadline while sharing its immutable date/time value.
     *
     * @param original Non-null deadline to copy.
     */
    private Deadline(Deadline original) {
        super(original);
        this.by = original.by;
    }

    /**
     * Returns an independent deadline snapshot; {@link LocalDateTime} itself is immutable.
     *
     * @return A new deadline with the same description, completion state, and due date/time.
     */
    @Override
    public Deadline copy() {
        return new Deadline(this);
    }

    /**
     * Returns the deadline's due date/time for chronological sorting.
     *
     * @return The due date/time.
     */
    @Override
    public LocalDateTime getSortDate() {
        return by;
    }

    /**
     * Returns the serialized deadline representation.
     *
     * @return A {@code D} record containing status, description, and a {@code yyyy-MM-dd HHmm} due date/time.
     */
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by.format(INPUT_FORMAT);
    }

    /**
     * Returns the user-facing deadline representation.
     *
     * @return The task type, status, description, and due date/time in {@code MMM dd yyyy, h:mm a} format.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(OUTPUT_FORMAT) + ")";
    }
}
