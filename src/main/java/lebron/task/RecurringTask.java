package lebron.task;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a RecurringTask, a Task that repeats at a fixed interval (e.g. a weekly
 * project meeting). A RecurringTask is never permanently "done": marking it as done
 * instead advances it to its next occurrence, so it keeps reappearing in the list.
 */
public class RecurringTask extends Task {
    private LocalDateTime nextDue;
    private final RecurrenceInterval interval;

    /**
     * Constructs a RecurringTask with the given description, next due date/time, and interval.
     *
     * @param description Task description.
     * @param nextDue The date/time this task's next occurrence is due.
     * @param interval How often this task recurs.
     */
    public RecurringTask(String description, LocalDateTime nextDue, RecurrenceInterval interval) {
        super(description);
        assert nextDue != null && interval != null : "RecurringTask's due date/time and interval should not be null";
        this.nextDue = nextDue;
        this.interval = interval;
    }

    /**
     * Marking a RecurringTask as done advances it to its next occurrence instead of
     * finishing it forever; unmarking has no effect, since it is never permanently done.
     *
     * @param status true to advance to the next occurrence, false to do nothing.
     */
    @Override
    public void setStatus(boolean status) {
        if (status) {
            this.nextDue = interval.advance(this.nextDue);
        }
    }

    @Override
    public String reformat() {
        return "R | " + super.reformat() + " | " + this.nextDue + " | " + this.interval.name();
    }

    @Override
    public String toString() {
        return "[R]" + super.toString() + " (every " + this.interval
                + ", next due: " + this.nextDue.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        RecurringTask other = (RecurringTask) obj;
        return nextDue.equals(other.nextDue) && interval == other.interval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nextDue, interval);
    }
}
