package duchess.tasks;

import java.time.LocalDate;

/**
 * Represents a task occurring within a specific range of time.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates a task occurring within a specific range of time.
     *
     * @param description The description of the task.
     * @param startDate The starting date of the task.
     * @param endDate The ending date of the task.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);

        assert (startDate != null) : "An event must have a start date";
        assert (endDate != null) : "An event must have an end date";
        assert (startDate.isBefore(endDate)) : "An event must start before it ends";

        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the string representation of a task occurring
     * in a specific range of time, in user-readable format.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)",
                super.toString(),
                this.startDate.format(DateFormat.DISPLAY_FORMAT),
                this.endDate.format(DateFormat.DISPLAY_FORMAT));
    }

    /**
     * Returns the string representation of a task occurring
     * in a specific range of time, in storage format.
     */
    @Override
    public String getStorageFormat() {
        return String.format("%s | E | /from %s /to %s",
                super.getStorageFormat(),
                this.startDate.format(DateFormat.PARSE_FORMAT),
                this.endDate.format(DateFormat.PARSE_FORMAT));
    }
}
