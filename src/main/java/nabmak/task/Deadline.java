package nabmak.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");
    private LocalDateTime deadline;

    /**
     * Creates Deadline object.
     *
     * @param desc description of the task
     * @param deadline date of deadline of the task
     */
    public Deadline(String desc, LocalDateTime deadline) {
        super(desc);
        this.deadline = deadline;
    }

    /**
     * Returns date of the deadline of task.
     *
     * @return date of deadline
     */
    public LocalDateTime getDead() {
        return this.deadline;
    }

    /**
     * Prints the task in format containing description and end date.
     *
     * @return task in String format
     */
    @Override
    public String toString() {
        return "[Deadline]" + super.toString() + " (by: " + deadline.format(DATE_FORMAT) + ")";
    }
}
