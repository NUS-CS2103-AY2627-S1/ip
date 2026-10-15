package neil.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a deadline date.
 */
public class DeadlineTask extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate deadline;

    /**
     * Creates a task with the specified description and deadline.
     *
     * @param description description of the task.
     * @param deadline date by which the task is due.
     */
    public DeadlineTask(String description, LocalDate deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString()
                + String.format(" (by: %s)", deadline.format(DISPLAY_DATE_FORMATTER));
    }

    @Override
    public String encode() {
        return String.format(
                "D | %d | %s | %s",
                isDone ? 1 : 0,
                description,
                deadline
        );
    }
}
