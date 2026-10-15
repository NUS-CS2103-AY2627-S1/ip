package duchess.tasks;

import java.time.LocalDate;
import java.time.Period;

/**
 * Represents a task that repeats after a fixed date interval.
 */
public class RecurringTask extends Task {
    private final LocalDate date;
    private final Period recurrence;

    /**
     * Creates a recurring task.
     *
     * @param description the description of the task
     * @param date the date of this occurrence
     * @param recurrence the interval before the next occurrence
     */
    public RecurringTask(String description, LocalDate date, Period recurrence) {
        super(description);

        assert (date != null) : "Recurring task date must not be null";
        assert (recurrence != null) : "Recurrence must not be null";
        assert (!recurrence.isZero()) : "Recurrence must not be zero";
        assert (!recurrence.isNegative()) : "Recurrence must not be negative";
        assert (recurrence.getYears() == 0 && recurrence.getMonths() == 0)
                : "Recurring task recurrence must be specified in days";

        this.date = date;
        this.recurrence = recurrence;
    }

    /**
     * Creates the next occurrence of this recurring task.
     *
     * @return a new task with its date advanced by the recurrence interval
     */
    public RecurringTask createNextOccurrence() {
        return new RecurringTask(
                getDescription(),
                this.date.plus(this.recurrence),
                this.recurrence);
    }

    private String getRecurrenceText() {
        int days = this.recurrence.getDays();
        return days + (days == 1 ? " day" : " days");
    }

    /**
     * Returns the storage representation of this recurring task.
     *
     * @return the task status, description, type, and recurrence period
     */
    @Override
    public String getStorageFormat() {
        return String.format("%s | R | /by %s /repeat %s",
                super.getStorageFormat(),
                this.date.format(DateFormat.PARSE_FORMAT),
                this.getRecurrenceText());
    }

    /**
     * Returns the user-readable representation of this recurring task.
     *
     * @return the task description and recurrence information
     */
    @Override
    public String toString() {
        return String.format("[R]%s (on: %s, recurs: %s)",
                super.toString(),
                this.date.format(DateFormat.DISPLAY_FORMAT),
                this.getRecurrenceText());
    }
}
