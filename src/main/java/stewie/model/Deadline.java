package stewie.model;

/**
 * Represents a task with a calendar-date deadline.
 */
public class Deadline extends Task {
    private final Date deadline;

    /**
     * Creates an unfinished task with a validated description and deadline date.
     *
     * @param description Description of the deadline task.
     * @param deadline Deadline date in a supported format.
     * @throws IllegalArgumentException If the description or deadline date is invalid.
     */
    public Deadline(String description, String deadline) {
        validateArgument(deadline, "Deadline date or time");
        super(description);
        this.deadline = new Date(deadline);
        assert this.deadline != null : "A deadline task must contain a Date";
    }

    /**
     * Returns the deadline formatted as {@code dd MMM uuuu}.
     */
    public String getDeadline() {
        return this.deadline.toString();
    }

    /**
     * Returns the deadline type marker, completion marker, description, and formatted deadline.
     */
    @Override
    public String toString() {
        return String.format("[D] [%s] %s (by: %s)",
                super.getStatusIcon(), super.getDescription(), this.deadline.toString());
    }
}
