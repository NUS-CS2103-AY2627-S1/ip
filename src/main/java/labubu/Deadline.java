package labubu;

import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline task with the given title and deadline.
     *
     * @param taskTitle Title of the task.
     * @param by Deadline of the task.
     */
    public Deadline(String taskTitle, LocalDateTime by) {
        super(taskTitle);
        this.by = by;
    }

    @Override
    public String getTaskDescription() {
        return taskTitle + " (by: " + by.format(OUTPUT_FORMATTER) + ")";
    }

    @Override
    public String getMarker() {
        return "D";
    }

    @Override
    public String toSaveFormat() {
        return getMarker() + "|" + getStatus().name() + "|" + taskTitle + "|" + by;
    }
}
