package mouse.task;

import mouse.MouseException;

/**
 * A task that must be done before a given date or time.
 */
public class Deadline extends Task {
    private final ParsedWhen by;

    /**
     * Creates a deadline from a date, date-time, or free-text {@code by} value.
     * Accepts {@code yyyy-MM-dd}, {@code yyyy-MM-dd HHmm}, {@code d/M/yyyy},
     * {@code d/M/yyyy HHmm}, or plain text such as {@code Friday}.
     *
     * @param description Task description.
     * @param by Deadline date, date-time, or free text.
     * @throws MouseException If {@code by} looks like a date but is invalid.
     */
    public Deadline(String description, String by) throws MouseException {
        super(description);
        this.by = ParsedWhen.parse(by, "The '/by' time");
    }

    /**
     * Returns the deadline with type letter and formatted {@code by} value.
     *
     * @return Display string for this deadline.
     */
    @Override
    public String toString() {
        return "[" + TaskType.DEADLINE.getSymbol() + "]" + super.toString()
                + " (by: " + by.toDisplayString() + ")";
    }

    /**
     * Returns the {@code /by} value in a form {@link Deadline} can parse again.
     *
     * @return ISO date, ISO date-time, or the original free text.
     */
    public String getByForSave() {
        return by.toSaveString();
    }

    /**
     * Returns this deadline in save format.
     *
     * @return Encoded deadline line.
     */
    @Override
    public String encode() {
        return encodeFields(TaskType.DEADLINE.getSymbol(), getByForSave());
    }

    /**
     * Returns whether {@code other} is the same deadline crumb.
     *
     * @param other Task to compare.
     * @return {@code true} if type, name, and due time match.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        if (!super.isDuplicateOf(other)) {
            return false;
        }
        Deadline deadline = (Deadline) other;
        return getByForSave().equalsIgnoreCase(deadline.getByForSave());
    }
}
