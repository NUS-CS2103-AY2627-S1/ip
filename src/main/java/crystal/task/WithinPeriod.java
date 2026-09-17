package crystal.task;

import java.time.LocalDate;

/**
 * Represents a task that can be completed within a scheduling period.
 */
public class WithinPeriod extends Task {
    /** Start date, time, or unrestricted scheduling text. */
    private final TaskDateTime from;

    /** End date, time, or unrestricted scheduling text. */
    private final TaskDateTime to;

    /**
     * Creates an incomplete task with a completion period.
     *
     * @param description description of the task.
     * @param from start date, time, or unrestricted text.
     * @param to end date, time, or unrestricted text.
     */
    public WithinPeriod(String description, String from, String to) {
        super(description);
        this.from = TaskDateTime.parse(from);
        this.to = TaskDateTime.parse(to);
    }

    /**
     * Returns this within-period task in the format stored on disk.
     *
     * @return pipe-separated within-period task data.
     */
    @Override
    public String toDataString() {
        return "W | " + super.toDataString()
                + " | " + from + " | " + to;
    }

    /**
     * Returns whether this task can be completed on the specified date. A dated
     * task with both a start and end date covers the inclusive range between them.
     *
     * @param date date to check.
     * @return {@code true} if the task can be completed on the date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate fromDate = from.getDate();
        LocalDate toDate = to.getDate();
        if (fromDate != null && toDate != null && !toDate.isBefore(fromDate)) {
            return !date.isBefore(fromDate) && !date.isAfter(toDate);
        }
        return from.occursOn(date) || to.occursOn(date);
    }

    /**
     * Returns this task with its type icon and completion period.
     *
     * @return formatted within-period task details.
     */
    @Override
    public String toString() {
        return "[W]" + super.toString()
                + " (from: " + from + " to: " + to + ")";
    }
}
