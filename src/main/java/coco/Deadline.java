package coco;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task that must be done by a specific date. May optionally recur (e.g. a
 * weekly project meeting) - marking a recurring deadline "done" advances it
 * to its next occurrence instead of completing it, since it never really
 * finishes.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    protected LocalDate by;
    private final Recurrence recurrence;

    /**
     * Creates a new, not-yet-done, non-recurring deadline.
     *
     * @param description Description of the deadline.
     * @param by Date the deadline is due by.
     */
    public Deadline(String description, LocalDate by) {
        this(description, by, null);
    }

    /**
     * Creates a new, not-yet-done deadline.
     *
     * @param description Description of the deadline.
     * @param by Date the deadline is due by.
     * @param recurrence How often this deadline recurs, or null if it
     *                   doesn't.
     */
    public Deadline(String description, LocalDate by, Recurrence recurrence) {
        super(description);
        // Parser.parseDeadline is responsible for rejecting an empty
        // description before ever reaching this constructor.
        assert !description.isEmpty() : "description should already be validated by Parser";
        this.by = by;
        this.recurrence = recurrence;
    }

    /** Returns whether this deadline recurs. */
    public boolean isRecurring() {
        return recurrence != null;
    }

    /**
     * Advances this deadline's date to its next occurrence.
     *
     * @throws IllegalStateException If this deadline isn't recurring.
     */
    public void advanceToNextOccurrence() {
        if (recurrence == null) {
            throw new IllegalStateException("Cannot advance a non-recurring deadline");
        }
        by = recurrence.nextOccurrence(by);
    }

    @Override
    public String toString() {
        String result = "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
        if (recurrence != null) {
            result += " (every: " + recurrence.name().toLowerCase() + ")";
        }
        return result;
    }

    @Override
    public String toSaveFormat() {
        String recurrenceField = recurrence == null ? "-" : recurrence.name();
        return "D | " + super.toSaveFormat() + " | " + by + " | " + recurrenceField;
    }
}
