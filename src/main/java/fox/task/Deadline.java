package fox.task;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a task with a deadline.
 */
public class Deadline extends Task {
    private final LocalDate byDate;
    private final String legacyBy;

    /**
     * Creates a new unfinished deadline task.
     *
     * @param description the task description
     * @param by the deadline time
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.byDate = by;
        this.legacyBy = null;
    }

    /**
     * Creates a legacy deadline whose free-form value is retained for old Fox data.
     * New commands should use the typed constructor.
     *
     * @param description the task description
     * @param by the legacy free-form deadline value
     */
    public Deadline(String description, String by) {
        super(description);
        this.byDate = null;
        this.legacyBy = by;
    }

    /**
     * Returns the typed deadline date, or {@code null} for legacy free-form data.
     *
     * @return the typed deadline date, or {@code null} when this is a legacy deadline
     */
    public LocalDate getByDate() {
        return byDate;
    }

    /**
     * Returns the exact value to write to Fox storage.
     *
     * @return the ISO date for a typed deadline or the original legacy text
     */
    public String getBy() {
        return byDate == null ? legacyBy : FoxDate.INPUT_FORMAT.format(byDate);
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && Objects.equals(getBy(), ((Deadline) other).getBy());
    }

    /**
     * Returns this deadline task in its display format.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        String displayValue = byDate == null ? legacyBy : FoxDate.format(byDate);
        return "[D]" + super.toString() + " (by: " + displayValue + ")";
    }
}
