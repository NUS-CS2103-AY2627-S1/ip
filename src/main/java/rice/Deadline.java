package rice;

import java.time.LocalDate;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private LocalDate deadline;

    /**
     * Creates a deadline task from its description and deadline date.
     *
     * @param task the task description
     * @param deadline the deadline in yyyy-MM-dd format
     */
    public Deadline(String task, String deadline) {
        super(task);
        this.deadline = Parser.parseDate(deadline);
    }

    /**
     * Returns the stored deadline string.
     *
     * @return the deadline in yyyy-MM-dd format
     */
    public String getDeadline() {
        return this.deadline.toString();
    }

    /**
     * Returns the deadline as a LocalDate for sorting and comparisons.
     *
     * @return the deadline date
     */
    @Override
    public LocalDate getDeadlineDate() {
        return this.deadline;
    }

    /**
     * Returns a formatted string representation of the deadline task.
     *
     * @return the formatted task description with a due date
     */
    @Override
    public String toString() {
        return "[D]" + super.completionMarker() + " " + super.getTask() + "(by: "
                + Parser.formatDate(this.deadline) + ")";
    }
}
