package jason.task;

import java.time.Duration;

/**
 * Represents a simple task without a deadline or event time.
 */
public class ToDo extends Task {
    private static final long MAX_DURATION_MINUTES = 24 * 60;
    private final Duration duration;

    /**
     * Creates an incomplete todo task.
     *
     * @param description text describing the task.
     */
    public ToDo(String description) {
        super(description);
        this.duration = null;
    }

    /**
     * Creates an incomplete todo task with a fixed duration.
     *
     * @param description text describing the task.
     * @param duration estimated duration of the task.
     * @throws IllegalArgumentException if the duration is invalid.
     */
    public ToDo(String description, Duration duration) {
        super(description);
        if (duration == null || duration.isZero() || duration.isNegative()
                || duration.toMinutes() > MAX_DURATION_MINUTES
                || !duration.equals(Duration.ofMinutes(duration.toMinutes()))) {
            throw new IllegalArgumentException("Todo duration must be between 1 minute and 24 hours.");
        }
        this.duration = duration;
    }

    /**
     * Returns the fixed duration of this todo, if one was specified.
     *
     * @return fixed duration, or null for a todo without a duration.
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Returns the todo type marker followed by the task display.
     *
     * @return formatted todo description.
     */
    @Override
    public String toString() {
        String taskText = "[T] " + super.toString();
        if (duration == null) {
            return taskText;
        }
        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        String durationText = hours > 0 && minutes > 0
                ? String.format("%dh %dm", hours, minutes)
                : hours > 0 ? String.format("%dh", hours) : String.format("%dm", minutes);
        return String.format("%s (for: %s)", taskText, durationText);
    }
}
