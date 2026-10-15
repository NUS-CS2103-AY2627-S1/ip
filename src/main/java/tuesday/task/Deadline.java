package tuesday.task;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a task with an optional deadline date and time.
 */
public class Deadline extends Task {
    private final LocalDate deadlineDate;
    private final LocalTime deadlineTime;

    /**
     * Creates a deadline task.
     *
     * @param name the task description.
     * @param deadlineDate the optional deadline date.
     * @param deadlineTime the optional deadline time.
     */
    public Deadline(String name, LocalDate deadlineDate, LocalTime deadlineTime) {
        super(name);
        this.deadlineDate = deadlineDate;
        this.deadlineTime = deadlineTime;
    }

    /**
     * Returns the task in its saved display format.
     *
     * @return the formatted deadline task.
     */
    @Override
    public String toString() {
        String deadline = DateTimeFormat.format(deadlineDate, deadlineTime);
        return "[D]" + super.toString() + " (by: " + deadline + ")";
    }
}
