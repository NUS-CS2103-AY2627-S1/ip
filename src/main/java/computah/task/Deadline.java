package computah.task;

import java.time.LocalDateTime;

import computah.util.DateTimeUtil;

/**
 * Represents a task that needs to be done before a specific date or time.
 */
public class Deadline extends Task {
    /**
     * Date/time by which this task should be completed.
     */
    private LocalDateTime by;

    /**
     * Creates a deadline task with the given description and due date/time.
     *
     * @param description description of the deadline task.
     * @param by date/time the task should be done by.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline task type icon.
     *
     * @return "D".
     */
    @Override
    public String getTaskType() {
        return "D";
    }

    /**
     * Converts this deadline into one line for the save file.
     *
     * @return save-file representation of this deadline.
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + DateTimeUtil.formatForFile(by);
    }

    /**
     * Returns the display representation of this deadline.
     *
     * @return task details formatted for display.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimeUtil.formatForDisplay(by) + ")";
    }
}
