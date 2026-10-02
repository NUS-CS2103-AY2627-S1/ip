package floppy;

import java.time.LocalDate;

/**
 * Represents a task that must be completed within a date range.
 */
public class PeriodTask extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates a task that can be completed between the specified dates.
     *
     * @param description Description of the task.
     * @param startDate First date on which the task can be completed.
     * @param endDate Last date on which the task can be completed.
     */
    public PeriodTask(String description, LocalDate startDate, LocalDate endDate) {
        super(description, TaskType.PERIOD);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the first date of the completion period.
     *
     * @return Start date of the completion period.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the last date of the completion period.
     *
     * @return End date of the completion period.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return super.toString() + " (within: " + startDate.format(DISPLAY_DATE_FORMAT)
                + " to " + endDate.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
