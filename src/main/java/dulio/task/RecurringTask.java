package dulio.task;

/**
 * A task that repeats at a user-specified interval.
 */
public class RecurringTask extends Task {
    private String interval;

    /**
     * Creates a recurring task.
     *
     * @param description The task description.
     * @param interval The interval at which the task repeats.
     */
    public RecurringTask(String description, String interval) {
        super(description);
        assert interval != null && !interval.isBlank() : "Recurring interval must not be blank";
        this.interval = interval;
    }

    @Override
    public String getTypeIcon() {
        return "R";
    }

    /**
     * Returns the interval at which this task repeats.
     *
     * @return The recurrence interval.
     */
    public String getInterval() {
        return interval;
    }

    /**
     * Returns the task description together with its recurrence interval.
     *
     * @return The formatted recurring task.
     */
    @Override
    public String toString() {
        return description + " (every: " + interval + ")";
    }
}
