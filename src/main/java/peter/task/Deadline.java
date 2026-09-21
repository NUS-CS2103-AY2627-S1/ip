package peter.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a task that must be completed by a particular date and time.
 */
public class Deadline extends Task {
    private final LocalDateTime dueDateTime;

    /**
     * Creates an incomplete deadline.
     *
     * @param description what the task says.
     * @param dueDateTime date and time the task is due, at midnight if the
     *     user gave only a date.
     */
    public Deadline(String description, LocalDateTime dueDateTime) {
        super(description, TaskType.DEADLINE);
        this.dueDateTime = dueDateTime;
    }

    /**
     * Returns the due date in brackets. The time is included only when the
     * deadline is not at midnight, so a date-only deadline reads naturally.
     */
    @Override
    public String getScheduleDetails() {
        return " (by: " + ScheduleDateTime.format(dueDateTime) + ")";
    }

    /**
     * {@inheritDoc}
     *
     * <p>A deadline stores the single date it is due by.
     */
    @Override
    protected List<String> getScheduleDataFields() {
        return List.of(dueDateTime.toString());
    }

    /**
     * Returns whether the deadline falls due on the given date.
     *
     * <p>Only the date is compared, so a deadline due at any time of day
     * matches its own date and no other.
     *
     * @param date date to check.
     * @return whether the task is due on the date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return dueDateTime.toLocalDate().equals(date);
    }
}
