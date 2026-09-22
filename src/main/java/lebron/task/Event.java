package lebron.task;

/** A task that spans a period, from a start date/time to an end date/time. */
public class Event extends Task {
    private DateTime from;
    private DateTime to;
    private final RecurrencePeriod recurrence;

    /**
     * Creates a one-off (non-recurring) event task.
     *
     * @param description what the event is
     * @param from when it starts
     * @param to when it ends
     */
    public Event(String description, DateTime from, DateTime to) {
        this(description, from, to, null);
    }

    /**
     * Creates an event task, optionally recurring.
     *
     * @param description what the event is
     * @param from when it starts
     * @param to when it ends
     * @param recurrence how often it repeats, or {@code null} for a one-off event
     */
    public Event(String description, DateTime from, DateTime to, RecurrencePeriod recurrence) {
        super(description);
        this.from = from;
        this.to = to;
        this.recurrence = recurrence;
    }

    /**
     * Marks this event done. If it recurs, rolls it forward to the next
     * occurrence instead: both endpoints advance by one {@link #recurrence}
     * (so the event keeps the same duration) and the task stays not done.
     */
    @Override
    public void markAsDone() {
        if (recurrence == null) {
            super.markAsDone();
        } else {
            from = recurrence.advance(from);
            to = recurrence.advance(to);
        }
    }

    @Override
    public boolean isRecurring() {
        return recurrence != null;
    }

    /**
     * Returns the data-file line, e.g. {@code E | 0 | camp | 2019-06-01 | 2019-06-03},
     * with the recurrence period appended as a 6th field when recurring, e.g.
     * {@code E | 0 | standup | 2025-01-06 0900 | 2025-01-06 0930 | week}.
     */
    @Override
    public String toFileFormat() {
        String line = "E | " + getDoneBit() + " | " + description + " | "
                + from.toFileFormat() + " | " + to.toFileFormat();
        return recurrence == null ? line : line + " | " + recurrence.toFileFormat();
    }

    /**
     * Returns the display form, e.g. {@code [E][ ] camp (from: Jun 01 2019 to: Jun 03 2019)},
     * with {@code (every: <period>)} appended when recurring.
     */
    @Override
    public String toString() {
        String shown = "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
        return recurrence == null ? shown : shown + " (every: " + recurrence.toFileFormat() + ")";
    }
}
