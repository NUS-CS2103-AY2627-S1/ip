package larper.task;

import java.time.LocalDate;

/**
 * Represents a task that should be completed by a specified date and optional time.
 */
public class Deadline extends Task {
    private LocalDate byDate;
    private String byTime;

    /**
     * Creates an unmarked deadline task using an ISO date string.
     *
     * @param description Text that describes the deadline task.
     * @param byDate Deadline date in ISO format.
     * @param byTime Deadline time in normalized form, or "no time".
     */
    public Deadline(String description, String byDate, String byTime) {
        this(description, LocalDate.parse(byDate), byTime);
    }

    /**
     * Creates an unmarked deadline task using a parsed date.
     *
     * @param description Text that describes the deadline task.
     * @param byDate Deadline date.
     * @param byTime Deadline time in normalized form, or "no time".
     */
    public Deadline(String description, LocalDate byDate, String byTime) {
        super(description);
        assert byDate != null : "Deadline date should be parsed before creating a deadline.";
        assert byTime != null && !byTime.isBlank() : "Deadline time should be normalized before creating a deadline.";
        this.byDate = byDate;
        this.byTime = byTime;
    }

    /**
     * Returns the type icon for deadline tasks.
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns a detached copy of this deadline task.
     */
    @Override
    public Task copy() {
        Deadline taskCopy = new Deadline(getDescription(), byDate, byTime);
        taskCopy.addTags(getTags());
        taskCopy.setDone(isDone());
        return taskCopy;
    }

    /**
     * Returns this deadline in the storage file format.
     */
    @Override
    public String toFileString() {
        assert byDate != null : "Deadline date should be available before saving.";
        assert byTime != null && !byTime.isBlank() : "Deadline time should be available before saving.";
        return getBaseFileString() + " | " + byDate + " | " + byTime + getTagsFileSuffix();
    }

    /**
     * Returns this deadline in the console display format.
     */
    @Override
    public String toString() {
        assert byDate != null : "Deadline date should be available before display.";
        assert byTime != null && !byTime.isBlank() : "Deadline time should be available before display.";
        String formattedDate = TaskDateTimeParser.formatDate(byDate);
        if (isNoTime()) {
            return formatWithTags(formatTaskWithoutTags() + " (by: " + formattedDate + ")");
        }
        return formatWithTags(formatTaskWithoutTags() + " (by: " + formattedDate + " " + byTime + ")");
    }

    private boolean isNoTime() {
        return byTime.trim().equalsIgnoreCase("no time");
    }
}
