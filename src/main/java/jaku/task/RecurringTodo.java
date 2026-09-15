package jaku.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a todo whose next occurrence repeats at a fixed interval. */
public class RecurringTodo extends Task {
    /** Formatter used for recurring todo dates shown in Jaku's user interface. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);

    /** Interval between occurrences. */
    private final Recurrence recurrence;

    /** Date of the occurrence currently tracked by this task. */
    private LocalDate nextOccurrence;

    /**
     * Creates an incomplete recurring todo.
     *
     * @param description text describing the task
     * @param nextOccurrence date of the first occurrence
     * @param recurrence interval between occurrences
     */
    public RecurringTodo(String description, LocalDate nextOccurrence, Recurrence recurrence) {
        super(description);
        this.nextOccurrence = nextOccurrence;
        this.recurrence = recurrence;
    }

    /**
     * Returns the interval between occurrences.
     *
     * @return this task's recurrence interval
     */
    public Recurrence getRecurrence() {
        return recurrence;
    }

    /**
     * Returns the date of the current occurrence.
     *
     * @return next scheduled occurrence date
     */
    public LocalDate getNextOccurrence() {
        return nextOccurrence;
    }

    /** Advances this task to its following occurrence. */
    public void advanceOccurrence() {
        nextOccurrence = recurrence.advance(nextOccurrence);
    }

    /** Restores this task to its previous occurrence. */
    public void reverseOccurrence() {
        nextOccurrence = recurrence.reverse(nextOccurrence);
    }

    /** Returns this recurring todo with its schedule details. */
    @Override
    public String toString() {
        return "[R]" + super.toString() + " (every: " + recurrence.getLabel()
                + ", next: " + DISPLAY_DATE_FORMAT.format(nextOccurrence) + ")";
    }
}
