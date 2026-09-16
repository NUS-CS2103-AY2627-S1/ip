package rambo.task;

import java.time.LocalDate;

import rambo.parser.Parser;

/**
 * Represents a deadline task with a name and a deadline date.
 */
public class DeadlineTask extends Task {
    private final LocalDate deadline;

    /**
     * Constructs a new deadline task with the given name and deadline string.
     *
     * @param taskName the name of the task
     * @param deadlineText the deadline date string in YYYY-MM-DD format
     */
    public DeadlineTask(String taskName, String deadlineText) {
        super(taskName);

        this.deadline = Parser.parseDate(deadlineText);
    }

    /**
     * Converts the task into the required string format for storage.
     *
     * @return the data string representation of the task
     */
    @Override
    public String toDataString() {
        return String.format("D|%s|%s|%s|%d",
                isDone() ? "X" : "", getTaskName(), deadline, getPriorityLevel());
    }

    /**
     * Returns a string representation of the task suitable for display.
     *
     * @return the task string representation
     */
    @Override
    public String toString() {
        return String.format("[D][%s]%s %s (by: %s)",
                isDone() ? "X" : "", getPriorityTag(), getTaskName(), deadline.format(DISPLAY_FORMAT));
    }
}
