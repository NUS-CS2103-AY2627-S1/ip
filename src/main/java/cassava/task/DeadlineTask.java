package cassava.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a Deadline-type task.
 */
public class DeadlineTask extends Task {

    private LocalDate date;

    /**
     * Default constructor for DeadlineTask.
     * @param description Text description of task.
     * @param completed Completion status.
     * @param taskPriority Priority of task.
     * @param date Date to complete task by.
     */
    public DeadlineTask(
            String description,
            boolean completed,
            TaskPriority taskPriority,
            LocalDate date
    ) {
        super(description, completed, taskPriority);
        this.date = date;
    }

    /**
     * Creates a DeadlineTask with completed = false,
     * and taskPriority = UNKNOWN.
     * @param description Text description of task.
     * @param date Date to complete task by.
     */
    public DeadlineTask(String description, LocalDate date) {
        super(description);
        this.date = date;
    }

    @Override
    public String toString() {
        return super.toString()
                + " (by: " + this.date.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ")";
    }

    @Override
    public String getTaskTypeToken() {
        return "D";
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | "
                + this.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
