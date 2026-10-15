package janet;

import java.time.LocalDate;

/**
 * Represents a task that takes place over a specified period.
 */
public class Event extends Task {

    protected static final String EVENT_FROM_SEP = "/from";
    protected static final String EVENT_TO_SEP = "/to";
    private static final String TASK_TYPE = "E";
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates a <code>Event</code> task type
     * with the specified completion status, type, label, starting date, and end date.
     *
     * @throws JanetException If the start event date is later than its ending date.
     */
    public Event(boolean isDone, String taskLabel, LocalDate from, LocalDate to) throws JanetException {
        super(isDone, Event.TASK_TYPE, taskLabel);
        if (from.isAfter(to)) {
            throw new JanetException("Event start date cannot be after end date!");
        }
        this.from = from;
        this.to = to;
    }


    @Override
    public Task markDone() throws JanetException {
        return new Event(true, super.taskLabel, this.from, this.to);
    }

    @Override
    public String toFileFormat() {
        return String.join(
                StorageTaskParser.INLINE_SEP,
                super.toFileFormat(),
                this.from.toString(),
                this.to.toString()
        );
    }

    @Override
    public String toString() {
        return String.format("%s (from: %s to: %s)",
                super.toString(),
                this.from.format(DateFormat.DATE_TIME_FORMAT),
                this.to.format(DateFormat.DATE_TIME_FORMAT)
        );
    }
}
