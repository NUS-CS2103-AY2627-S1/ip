package cooper.task;

import java.time.LocalDateTime;

import cooper.ui.Ui;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    private final LocalDateTime dueDate;

    /** Creates an incomplete deadline with the specified description and due date. */
    public Deadline(String description, LocalDateTime dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /** Creates a deadline with the specified description, status, and due date. */
    public Deadline(String description, boolean isDone, LocalDateTime dueDate) {
        super(description, isDone);
        this.dueDate = dueDate;
    }

    /** Returns the display form of this deadline, including its due date. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + String.format(" (by: %s)", Ui.formatDate(dueDate));
    }

    /** Returns the storage representation of this deadline. */
    @Override
    public String toDataString() {
        return String.format("%s | %s | %s", "D", super.toDataString(), dueDate);
    }
}
