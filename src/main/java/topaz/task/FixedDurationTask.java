package topaz.task;

/**
 * Represents a task that has a fixed estimated duration but no scheduled time.
 */
public class FixedDurationTask extends Task {
    private static final long MINUTES_PER_HOUR = 60;
    private final long durationMinutes;

    /**
     * Creates an incomplete task with the given fixed duration.
     *
     * @param description the task description
     * @param durationMinutes the positive duration in whole minutes
     */
    public FixedDurationTask(String description, long durationMinutes) {
        super(description);
        assert durationMinutes > 0 : "A fixed-duration task must take a positive number of minutes.";
        this.durationMinutes = durationMinutes;
    }

    /**
     * Returns the fixed-duration task type.
     *
     * @return the fixed-duration task type
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.FIXED_DURATION;
    }

    /**
     * Returns the description together with the formatted fixed duration.
     *
     * @return the formatted task description
     */
    @Override
    public String getDescription() {
        return super.getDescription() + " (for: " + formatDuration() + ")";
    }

    @Override
    protected boolean hasSameSchedule(Task other) {
        return durationMinutes == ((FixedDurationTask) other).durationMinutes;
    }

    /**
     * Serializes this task for storage.
     *
     * @return the fixed-duration task in save-file format
     */
    @Override
    public String toFileString() {
        return "F | " + (isDone() ? "1" : "0") + " | " + super.getDescription()
                + " | " + durationMinutes;
    }

    /**
     * Returns the duration using hours when it is an exact number of hours.
     */
    private String formatDuration() {
        if (durationMinutes % MINUTES_PER_HOUR == 0) {
            long hours = durationMinutes / MINUTES_PER_HOUR;
            return hours + (hours == 1 ? " hour" : " hours");
        }
        return durationMinutes + (durationMinutes == 1 ? " minute" : " minutes");
    }
}
