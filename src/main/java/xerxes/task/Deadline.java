package xerxes.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a task that must be completed by a particular date.
 */
public class Deadline extends Task {
    /** Format used when displaying the deadline to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /** Date by which this task should be completed. */
    private final LocalDate deadline;

    /**
     * Creates an incomplete deadline task.
     *
     * @param taskName Description of the task.
     * @param deadline Date by which the task should be completed.
     */
    public Deadline(String taskName, LocalDate deadline) {
        super(taskName);
        assert deadline != null : "A deadline task must have a deadline date";
        this.deadline = deadline;
    }

    /**
     * Returns the deadline date.
     *
     * @return Deadline date for this task.
     */
    public LocalDate getDeadline() {
        return this.deadline;
    }

    /**
     * Checks whether another deadline has the same description and deadline date.
     *
     * @param other Object to compare with this deadline.
     * @return True if both objects represent equivalent deadlines.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }

        Deadline deadlineTask = (Deadline) other;
        return deadline.equals(deadlineTask.deadline);
    }

    /**
     * Returns a hash code based on the deadline's task details.
     *
     * @return Hash code for this deadline.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deadline);
    }

    /**
     * Returns a display representation including this task's deadline.
     *
     * @return Formatted deadline task description.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + this.deadline.format(DISPLAY_FORMAT) + ")";
    }

}
