package bott.task;

/**
 * Represents a task that takes a fixed amount of time but has no fixed
 * start or end, e.g. "read the sales report" which needs two hours.
 */
public class FixedDurationTask extends Task {

    private static final int MINUTES_PER_HOUR = 60;

    private final int durationMinutes;

    /**
     * Creates a new fixed-duration task.
     *
     * @param description Description of the task.
     * @param durationMinutes Time the task needs, in minutes; must be positive.
     */
    public FixedDurationTask(String description, int durationMinutes) {
        super(description, TaskType.FIXED_DURATION);
        this.durationMinutes = durationMinutes;
    }

    /**
     * Returns this task's textual representation, e.g.
     * "[F][ ] read sales report (for: 2h)".
     */
    @Override
    public String toString() {
        return super.toString() + " (for: " + formatDuration() + ")";
    }

    /**
     * Returns this task's representation for saving to file, e.g.
     * "F | 0 | read sales report | 120".
     */
    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + durationMinutes;
    }

    /**
     * Returns the duration as an "Xh Ym" string, dropping a component that
     * is zero, e.g. "2h", "45m", or "1h 30m".
     */
    private String formatDuration() {
        int hours = durationMinutes / MINUTES_PER_HOUR;
        int minutes = durationMinutes % MINUTES_PER_HOUR;
        if (hours > 0 && minutes > 0) {
            return hours + "h " + minutes + "m";
        }
        if (hours > 0) {
            return hours + "h";
        }
        return minutes + "m";
    }
}
