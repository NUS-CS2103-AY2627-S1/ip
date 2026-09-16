package chattingheads.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specified date and time.
 */
public class Deadline extends Task {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String TYPE = "D";
    private LocalDateTime dueDateTime;

    /**
     * Creates an incomplete deadline task with the given description and deadline.
     *
     * @param description Description of the task.
     * @param dueDateTime Date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime dueDateTime) {
        super(description);
        this.dueDateTime = dueDateTime;
    }

    /**
     * Creates a deadline task with the given description, completion status,
     * and deadline.
     *
     * @param description Description of the task.
     * @param isDone      Completion status of the task.
     * @param dueDateTime Date and time by which the task should be completed.
     */
    public Deadline(String description, boolean isDone, LocalDateTime dueDateTime) {
        super(description, isDone);
        this.dueDateTime = dueDateTime;
    }

    @Override
    public String toString() {
        return String.format(
                "[%s]%s (by: %s)",
                TYPE,
                super.toString(),
                DATE_TIME_FORMATTER.format(dueDateTime)
        );
    }

    @Override
    public String toCsv() {
        return String.format("%s,%s,%s", TYPE, super.toCsv(), dueDateTime);
    }

    /**
     * Changes the due date and time of the deadline task.
     *
     * @param newDueDateTime New due date and time.
     */
    public void reschedule(LocalDateTime newDueDateTime) {
        dueDateTime = newDueDateTime;
    }
}
