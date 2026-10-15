package probe.task;

import java.time.LocalDateTime;

import probe.parser.DateTimeParser;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Creates a deadline task with its description and due time.
     *
     * @param description Description of the deadline.
     * @param by Time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline in a display-friendly format.
     *
     * @return Display representation of the deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + DateTimeParser.formatForDisplay(by) + ")";
    }

    /**
     * Returns the deadline in storage format.
     *
     * @return Storage representation of the deadline.
     */
    @Override
    public String toFileFormat() {
        String tagsText = tagsToFileFormat();
        return String.format("D | %d | %s | %s%s", isDone ? 1 : 0, description,
                DateTimeParser.formatForStorage(by), tagsText.isEmpty() ? "" : " | " + tagsText);
    }
}
