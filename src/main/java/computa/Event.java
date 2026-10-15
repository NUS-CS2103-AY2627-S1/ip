package computa;
/**
 * Represents an event task that occurs within a specific start and end time frame.
 */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event extends Deadline {
    protected LocalDateTime start;
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a");
    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     * Creates an Event task with a specified description, start time, and end time.
     * The task is marked as undone by default.
     *
     * @param description The text description of the event.
     * @param start       The starting date and time, expected in yyyy-MM-dd HHmm format.
     * @param deadline    The ending date and time, expected in yyyy-MM-dd HHmm format.
     * @throws DateTimeParseException If the date strings do not match the expected format.
     */


    public Event(String description, String start, String deadline) {
        super(description, deadline);
        assert start != null && !start.isBlank() : "An event must have a start date";
        this.start = LocalDateTime.parse(start, INPUT_FORMAT);
    }
    /**
     * Creates an Event task with a specified description, start time, end time, and completion status.
     *
     * @param description The text description of the event.
     * @param start       The starting date and time, expected in yyyy-MM-dd HHmm format.
     * @param deadline    The ending date and time, expected in yyyy-MM-dd HHmm format.
     * @param isDone      The completion status of the task.
     * @throws DateTimeParseException If the date strings do not match the expected format.
     */

    public Event(String description, String start, String deadline, boolean isDone) {
        super(description, deadline, isDone);
        assert start != null && !start.isBlank() : "An event must have a start date";
        this.start = LocalDateTime.parse(start, INPUT_FORMAT);
    }

    @Override
    public String getTaskType() {
        return "[E]";
    }

    @Override
    public String getTaskDescription() {
        return getTaskType() + getStatusIcon() + " " + description + " (from: "
                + start.format(DISPLAY_FORMAT) + " to: " + deadline.format(DISPLAY_FORMAT) + ")\n";
    }

    @Override
    public String toFileFormat() {
        int completionStatus = isDone ? 1 : 0;
        return "E | " + completionStatus + " | " + description + " |"
                + start.format(STORAGE_FORMAT) + " |" + deadline.format(STORAGE_FORMAT);
    }
}
