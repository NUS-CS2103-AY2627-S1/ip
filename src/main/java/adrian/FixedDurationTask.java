package adrian;

/**
 * Represents an unscheduled task that requires a fixed amount of time.
 */
public class FixedDurationTask extends Task {
    /** Number of minutes needed to complete this task. */
    private final int durationInMinutes;

    /**
     * Creates an incomplete task with the given description and duration.
     *
     * @param description description of the task.
     * @param durationInMinutes number of minutes needed to complete the task.
     * @throws IllegalArgumentException if the duration is not positive.
     */
    public FixedDurationTask(String description, int durationInMinutes) {
        super(description, TaskType.FIXED_DURATION);

        if (durationInMinutes <= 0) {
            throw new IllegalArgumentException("Task duration must be positive");
        }

        this.durationInMinutes = durationInMinutes;
    }

    /**
     * Returns the task in the format used for persistent storage.
     *
     * @return serialized fixed-duration task data.
     */
    @Override
    public String toDataString() {
        return "F | " + super.toDataString()
                + " | " + durationInMinutes;
    }

    /**
     * Returns a display-friendly representation containing the required duration.
     *
     * @return formatted fixed-duration task description.
     */
    @Override
    public String toString() {
        return super.toString()
                + " (duration: " + durationInMinutes + " minutes)";
    }
}
