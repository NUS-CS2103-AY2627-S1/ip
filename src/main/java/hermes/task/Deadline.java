package hermes.task;

import java.time.LocalDateTime;

/** A task that must be finished by a given moment. */
public class Deadline extends Task {

    protected LocalDateTime dueDateTime;

    /** Creates a deadline that is not yet completed. */
    public Deadline(String taskDescription, LocalDateTime dueDateTime) {
        super(taskDescription);
        this.dueDateTime = dueDateTime;
    }

    /** Creates a deadline in a known state from its stored ISO-8601 date. */
    public Deadline(boolean isCompleted, String taskDescription, String deadline) {
        super(isCompleted, taskDescription);
        this.dueDateTime = LocalDateTime.parse(deadline);
    }

    @Override
    public Deadline copy() {
        return new Deadline(this.isCompleted, this.taskDescription, this.dueDateTime.toString());
    }

    @Override
    public LocalDateTime getDueDateTime() {
        return this.dueDateTime;
    }

    @Override
    public String getFileContent() {
        return String.format("%s | %d | %s | %s",
                TaskType.DEADLINE.getSymbol(),
                this.isCompleted ? 1 : 0,
                this.taskDescription,
                this.dueDateTime);
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)",
                super.toString(),
                this.dueDateTime.format(DISPLAY_FORMATTER));
    }
}
