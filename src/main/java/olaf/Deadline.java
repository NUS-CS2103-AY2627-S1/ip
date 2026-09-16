package olaf;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Represents a task that must be completed by a given date.
 */
final class Deadline extends Task {
    /** Identifies a deadline task in storage and displayed responses. */
    static final String TYPE_CODE = "D";

    private LocalDate dueDate;

    Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    private Deadline(Deadline original) {
        super(original);
        dueDate = original.dueDate;
    }

    /** {@inheritDoc} */
    @Override
    Task copy() {
        return new Deadline(this);
    }

    /**
     * Replaces the due date while preserving the task's description and completion status.
     *
     * @param dueDate replacement due date
     * @throws NullPointerException if the date is null
     */
    void reschedule(LocalDate dueDate) {
        this.dueDate = Objects.requireNonNull(dueDate, "dueDate");
    }

    /** {@inheritDoc} */
    @Override
    String getTypeCode() {
        return TYPE_CODE;
    }

    /** {@inheritDoc} */
    @Override
    List<String> getAdditionalStorageFields() {
        return List.of(dueDate.toString());
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (by: " + TaskDateFormat.format(dueDate) + ")";
    }
}
