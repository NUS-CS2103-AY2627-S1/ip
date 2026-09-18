package habpyduck.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import habpyduck.storage.Storage;

/**
 * Represents a task that starts at a specific date or time and ends at another.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates an event task with the given description, start, and end.
     *
     * @param description the text that describes the event.
     * @param from the date and time when the event starts.
     * @param to the date and time when the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event in the simple text format used when saving tasks.
     *
     * @return a line of text that can be written to the save file.
     */
    @Override
    public String toFileString() {
        return Storage.EVENT_TASK_TYPE + Storage.FILE_FIELD_SEPARATOR + getDoneStatus()
                + Storage.FILE_FIELD_SEPARATOR + Storage.escapeFileField(description)
                + Storage.FILE_FIELD_SEPARATOR + from
                + Storage.FILE_FIELD_SEPARATOR + to
                + formatTagsForFile();
    }

    /**
     * Returns the event task in the format shown to users.
     *
     * @return the task type, status icon, description, start, and end.
     */
    @Override
    public String toString() {
        return "[E]" + getBaseDisplayText() + " (from: " + from.format(DISPLAY_DATE_TIME_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_TIME_FORMAT) + ")" + getDisplayTags();
    }
}
