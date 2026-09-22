package lebron.task;

/** A task that must be completed by a given date (and optionally time). */
public class Deadline extends Task {
    private DateTime by;
    private final RecurrencePeriod recurrence;

    /**
     * Creates a one-off (non-recurring) deadline task.
     *
     * @param description what needs to be done
     * @param by when it is due
     */
    public Deadline(String description, DateTime by) {
        this(description, by, null);
    }

    /**
     * Creates a deadline task, optionally recurring.
     *
     * @param description what needs to be done
     * @param by when it is due
     * @param recurrence how often it repeats, or {@code null} for a one-off deadline
     */
    public Deadline(String description, DateTime by, RecurrencePeriod recurrence) {
        super(description);
        this.by = by;
        this.recurrence = recurrence;
    }

    /**
     * Marks this deadline done. If it recurs, rolls it forward to the next
     * occurrence instead: the due date advances by one {@link #recurrence}
     * and the task stays not done, ready for the next occurrence.
     */
    @Override
    public void markAsDone() {
        if (recurrence == null) {
            super.markAsDone();
        } else {
            by = recurrence.advance(by);
        }
    }

    @Override
    public boolean isRecurring() {
        return recurrence != null;
    }

    /**
     * Returns the data-file line, e.g. {@code D | 0 | return book | 2019-12-02},
     * with the recurrence period appended as a 5th field when recurring, e.g.
     * {@code D | 0 | pay rent | 2025-01-01 | month}.
     */
    @Override
    public String toFileFormat() {
        String line = "D | " + getDoneBit() + " | " + description + " | " + by.toFileFormat();
        return recurrence == null ? line : line + " | " + recurrence.toFileFormat();
    }

    /**
     * Returns the display form, e.g. {@code [D][ ] return book (by: Dec 02 2019)},
     * with {@code (every: <period>)} appended when recurring.
     */
    @Override
    public String toString() {
        String shown = "[D]" + super.toString() + " (by: " + by + ")";
        return recurrence == null ? shown : shown + " (every: " + recurrence.toFileFormat() + ")";
    }
}
