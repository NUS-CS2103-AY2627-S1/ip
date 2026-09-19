package lizzy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that can be completed during an inclusive date period.
 */
public class WithinPeriodTask extends Task {
    /**
     * The format used to present period dates to the user.
     */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /**
     * The first date on which the task may be completed.
     */
    private final LocalDate from;

    /**
     * The last date on which the task may be completed.
     */
    private final LocalDate to;

    /**
     * Creates an incomplete task that can be completed during the given period.
     *
     * @param description the text describing the task
     * @param from the first date in the inclusive period
     * @param to the last date in the inclusive period
     */
    public WithinPeriodTask(String description, LocalDate from, LocalDate to) {
        this(description, from, to, false);
    }

    /**
     * Restores a period task with its saved completion state.
     *
     * @param description the text describing the task
     * @param from the first date in the inclusive period
     * @param to the last date in the inclusive period
     * @param isDone whether the task has been completed
     */
    public WithinPeriodTask(String description, LocalDate from, LocalDate to, boolean isDone) {
        super(description, isDone);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Completion-period dates cannot be null.");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("A period's end date cannot be before its start date.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether another task has the same description and inclusive completion period.
     *
     * @param other the task to compare
     * @return {@code true} if both period tasks have the same details
     */
    @Override
    boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && from.equals(((WithinPeriodTask) other).from)
                && to.equals(((WithinPeriodTask) other).to);
    }

    /**
     * Returns whether the date falls within this task's inclusive completion period.
     *
     * @param date the date to check
     * @return {@code true} if the date is within the period
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    /**
     * Returns the first date in this task's completion period.
     *
     * @return the period's start date
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the last date in this task's completion period.
     *
     * @return the period's end date
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns this period task with its type marker and inclusive completion period.
     *
     * @return the display-ready period-task representation
     */
    @Override
    public String toString() {
        return "[W]" + super.toString() + " (within: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
