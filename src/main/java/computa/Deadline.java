package computa;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Represents a task that needs to be completed by a specific date and time.
 */

public class Deadline extends Todo {
    protected LocalDateTime deadline;
    /**
     * Creates a Deadline task with a specified description and due date.
     *
     * @param description The text description of the deadline task.
     * @param deadline       The due date and time, expected in yyyy-MM-dd HHmm format.
     * @throws DateTimeParseException If the date string does not match the expected format.
     */
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy h.mma");
    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    public Deadline(String description, String deadline) {
        super(description);
        assert deadline != null && !deadline.isBlank() : "A deadline must have a date";
        this.deadline = LocalDateTime.parse(deadline, INPUT_FORMAT);
    }
    /**
     * Creates a Deadline task with a specified description, due date, and completion status.
     *
     * @param description The text description of the deadline task.
     * @param deadline    The due date and time, expected in yyyy-MM-dd HHmm format.
     * @param isDone      The completion status of the task.
     * @throws DateTimeParseException If the date string does not match the expected format.
     */

    public Deadline(String description, String deadline, boolean isDone) {
        super(description, isDone);
        assert deadline != null && !deadline.isBlank() : "A deadline must have a date";
        this.deadline = LocalDateTime.parse(deadline, INPUT_FORMAT);
    }

    @Override
    public String getTaskType() {
        return "[D]";
    }

    @Override
    public String getTaskDescription() {
        return getTaskType() + getStatusIcon() + " " + description + " (by: "
                + deadline.format(DISPLAY_FORMAT) + ")\n";
    }

    @Override
    public String toFileFormat() {
        int completionStatus = isDone ? 1 : 0;
        return "D | " + completionStatus + " | " + description + " |"
                + deadline.format(STORAGE_FORMAT);
    }
}
