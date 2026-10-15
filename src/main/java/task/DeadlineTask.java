package task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that has a deadline.
 */
public class DeadlineTask extends Task {
    private static final DateTimeFormatter DEFAULT_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateTimeFormatter timeFormat = DEFAULT_TIME_FORMAT;
    private LocalDateTime deadline = LocalDateTime.now().plusHours(1);

    /**
     * Creates an undone deadline task.
     *
     * @param description the task description
     * @param deadline the deadline date and time
     */
    public DeadlineTask(String description, LocalDateTime deadline) {
        super(description);
        assert deadline != null : "Deadline tasks must have a deadline";
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.deadline = deadline;
    }

    /**
     * Creates an undone deadline task using the default date format.
     *
     * @param description the task description
     * @param deadline the deadline as a formatted string
     */
    public DeadlineTask(String description, String deadline) {
        super(description);
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.deadline = LocalDateTime.parse(deadline, this.timeFormat);
    }

    /**
     * Creates an undone deadline task using a custom date format.
     *
     * @param description the task description
     * @param deadline the deadline as a formatted string
     * @param format the custom format for parsing the deadline
     */
    public DeadlineTask(String description, String deadline, String format) {
        super(description);
        DateTimeFormatter tempFormat = DateTimeFormatter.ofPattern(format);
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.deadline = LocalDateTime.parse(deadline, tempFormat);
    }

    /**
     * Creates a deadline task with a specified completion state.
     *
     * @param description the task description
     * @param done whether the task is already complete
     * @param deadline the deadline date and time
     */
    public DeadlineTask(String description, boolean done, LocalDateTime deadline) {
        super(description, done);
        assert deadline != null : "Deadline tasks must have a deadline";
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.deadline = deadline;
    }

    /**
     * Creates a deadline task with a custom format and completion state.
     *
     * @param description the task description
     * @param done whether the task is already complete
     * @param deadline the deadline as a formatted string
     * @param format the custom format for parsing the deadline
     */
    public DeadlineTask(String description, boolean done, String deadline, String format) {
        super(description, done);
        DateTimeFormatter tempFormat = DateTimeFormatter.ofPattern(format);
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.deadline = LocalDateTime.parse(deadline, tempFormat);
    }

    /**
     * Creates a deadline task with a specified completion state using the default format.
     *
     * @param description the task description
     * @param done whether the task is already complete
     * @param deadline the deadline as a formatted string
     */
    public DeadlineTask(String description, boolean done, String deadline) {
        super(description, done);
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.deadline = LocalDateTime.parse(deadline, this.timeFormat);
    }

    /**
     * Returns the deadline of the task.
     *
     * @return the deadline date and time
     */
    public LocalDateTime getDeadline() {
        return deadline;
    }

    /**
     * Checks whether another deadline task has the same description and deadline.
     * Completion state is not considered when comparing tasks.
     *
     * @param other the object to compare with this task
     * @return true if both objects represent the same deadline task
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        DeadlineTask otherTask = (DeadlineTask) other;
        return this.deadline.equals(otherTask.deadline);
    }

    @Override
    public String toSignature() {
        if (this.isDone()) {
            return "D|1|" + getDescription() + "|" + this.deadline.format(timeFormat);
        }
        return "D|0|" + getDescription() + "|" + this.deadline.format(timeFormat);
    }

    @Override
    public String toString() {
        if (this.isDone()) {
            return "[D][X] " + getDescription() + " (by: " + this.deadline.format(timeFormat) + ")";
        }
        return "[D][ ] " + getDescription() + " (by: " + this.deadline.format(timeFormat) + ")";
    }
}
