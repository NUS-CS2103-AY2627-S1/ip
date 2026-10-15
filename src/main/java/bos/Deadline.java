package bos;

/**
 * Represents a task that should be completed by a particular time.
 */
public class Deadline extends Task {
    static final String STORAGE_TYPE = "D";

    private final String deadline;

    /**
     * Creates a deadline with the given description and due time.
     *
     * @param description task description.
     * @param deadline deadline date-time text.
     */
    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = deadline;
    }

    /**
     * Returns a readable representation of this deadline and its due time.
     *
     * @return formatted deadline for display.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + Parser.formatDateTimeForDisplay(this.deadline) + ")";
    }

    /**
     * Converts this deadline into the text-file storage format.
     *
     * @return storage representation of this deadline.
     */
    @Override
    public String formatForStorage() {
        return formatStorageFields(STORAGE_TYPE, deadline);
    }
}
