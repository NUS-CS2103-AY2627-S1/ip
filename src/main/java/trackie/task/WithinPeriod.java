package trackie.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that may be completed within a specified date range. */
public class WithinPeriod extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an incomplete task that may be done between two inclusive dates.
     *
     * @param description description of the task
     * @param from first date on which the task may be completed
     * @param to last date on which the task may be completed
     */
    public WithinPeriod(String description, LocalDate from, LocalDate to) {
        super(description, TaskType.WITHIN_PERIOD);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the task with its type, status, and completion period.
     *
     * @return formatted within-period task
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + this.from.format(DISPLAY_FORMAT)
                + " to: " + this.to.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns a data-file line containing the task's completion period.
     *
     * @return serialized within-period task
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + this.from + " | " + this.to;
    }
}
