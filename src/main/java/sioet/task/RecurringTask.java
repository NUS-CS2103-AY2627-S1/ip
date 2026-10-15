package sioet.task;

/**
 * Represents a task that recurs at a fixed interval.
 */
public class RecurringTask extends Task {
    private final String recurrence;

    /**
     * Creates a recurring task with the given description and recurrence.
     *
     * @param description the task description
     * @param recurrence  the recurrence interval
     */
    public RecurringTask(String description, String recurrence) {
        super(description);
        assert recurrence != null : "Recurrence should not be null";
        this.recurrence = recurrence;
    }

    @Override
    protected String getTaskType() {
        return "R";
    }

    @Override
    protected String getDetails() {
        return " (every " + recurrence + ")";
    }

    /**
     * Returns the recurrence interval.
     *
     * @return the recurrence interval
     */
    public String getRecurrence() {
        return recurrence;
    }
}
