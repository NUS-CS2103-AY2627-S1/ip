package nova.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import nova.DateTimes;

/**
 * A task that has to be finished by a particular moment.
 */
public class Deadline extends Task {
    /** When the task is due. Midnight means the user gave only a date. */
    private final LocalDateTime by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description what the user called this task
     * @param by          when it is due
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }


    /**
     * Returns whether this deadline is due on the given date.
     *
     * @param date the date to test against
     * @return true if the deadline falls on that date
     */
    @Override
    public boolean isOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * Returns when this deadline is due, which is the moment it sorts by.
     *
     * @return the due date and time
     */
    @Override
    public Optional<LocalDateTime> getScheduledTime() {
        return Optional.of(by);
    }

    @Override
    protected String identity() {
        return super.identity() + "|" + by;
    }

    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + DateTimes.toFileString(by);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimes.format(by) + ")";
    }
}
