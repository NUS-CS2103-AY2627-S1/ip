package chatty.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task whose next occurrence advances on a repeating schedule.
 */
public class RecurringTask extends Task {
    /** Format used to display the next occurrence date. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Date of the next pending occurrence. */
    private LocalDate nextOccurrenceDate;

    /** Positive number of recurrence units between occurrences. */
    private final int interval;

    /** Calendar unit used by the recurrence interval. */
    private final RecurrenceUnit recurrenceUnit;

    /**
     * Creates an incomplete recurring task with the supplied schedule.
     *
     * @param description description of the recurring task.
     * @param nextOccurrenceDate date of the next pending occurrence.
     * @param interval positive number of units between occurrences.
     * @param recurrenceUnit calendar unit used by the interval.
     */
    public RecurringTask(String description, LocalDate nextOccurrenceDate, int interval,
                         RecurrenceUnit recurrenceUnit) {
        super(description);
        assert nextOccurrenceDate != null : "Recurring task date must not be null";
        assert interval > 0 : "Recurring task interval must be positive";
        assert recurrenceUnit != null : "Recurring task unit must not be null";
        this.nextOccurrenceDate = nextOccurrenceDate;
        this.interval = interval;
        this.recurrenceUnit = recurrenceUnit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeIcon() {
        return "R";
    }

    /**
     * Returns the date of the next pending occurrence.
     *
     * @return next occurrence date.
     */
    public LocalDate getNextOccurrenceDate() {
        return nextOccurrenceDate;
    }

    /**
     * Returns the number of recurrence units between occurrences.
     *
     * @return positive recurrence interval.
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Returns the calendar unit used by this task's recurrence interval.
     *
     * @return recurrence unit.
     */
    public RecurrenceUnit getRecurrenceUnit() {
        return recurrenceUnit;
    }

    /**
     * Rejects direct status completion because recurring tasks remain active between occurrences.
     *
     * @throws UnsupportedOperationException always, because the schedule must be advanced instead.
     */
    @Override
    public void markAsDone() {
        throw new UnsupportedOperationException(
                "Recurring tasks advance instead of becoming completed");
    }

    /** Advances this task from its current occurrence to the next one. */
    public void advanceToNextOccurrence() {
        nextOccurrenceDate = recurrenceUnit.addTo(nextOccurrenceDate, interval);
    }

    /**
     * Returns this task with its next date and recurrence interval.
     *
     * @return formatted recurring-task information.
     */
    @Override
    public String toString() {
        return super.toString() + " (on: " + nextOccurrenceDate.format(DISPLAY_FORMAT)
                + ", every: " + recurrenceUnit.formatInterval(interval) + ")";
    }
}
