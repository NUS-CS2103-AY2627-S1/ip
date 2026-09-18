package habpyduck.task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import habpyduck.storage.Storage;

/**
 * Represents a task that needs to be done before a specific date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    protected LocalDateTime by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description the text that describes the task.
     * @param by the date and time by which the task should be done.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns this deadline in the simple text format used when saving tasks.
     *
     * @return a line of text that can be written to the save file.
     */
    @Override
    public String toFileString() {
        return Storage.DEADLINE_TASK_TYPE + Storage.FILE_FIELD_SEPARATOR + getDoneStatus()
                + Storage.FILE_FIELD_SEPARATOR + Storage.escapeFileField(description)
                + Storage.FILE_FIELD_SEPARATOR + by
                + formatTagsForFile();
    }

    /**
     * Returns the deadline task in the format shown to users.
     *
     * @return the task type, status icon, description, and deadline.
     */
    @Override
    public String toString() {
        String formattedDateTime = by.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? by.format(DISPLAY_DATE_FORMAT)
                : by.format(DISPLAY_DATE_TIME_FORMAT);
        return "[D]" + getBaseDisplayText() + " (by: " + formattedDateTime + ")" + getDisplayTags();
    }
}
