package yappa.task;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import yappa.util.DateUtil;

/**
 * Represents a task with a deadline date/time constraint.
 */
public class Deadline extends Task {
    /** Date and time by which this task should be completed. */
    private final LocalDateTime by;

    /**
     * Creates a new deadline task.
     *
     * @param description Task description.
     * @param by          Deadline date and time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Creates a deadline task with the known completion state.
     *
     * @param description Task description.
     * @param isDone      Whether the task is completed.
     * @param by          Deadline date and time.
     */
    public Deadline(String description, boolean isDone, LocalDateTime by) {
        super(description, isDone);
        this.by = by;
    }

    public LocalDateTime getBy() {
        return this.by;
    }

    @Override
    public Optional<LocalDateTime> getDateTime() {
        return Optional.of(getBy());
    }

    /**
     * Formats this deadline for file storage.
     *
     * @return Storage representation of this deadline.
     */
    @Override
    public String toFileString() {
        return TaskType.DEADLINE.getCode()
                + " | " + (isDone() ? "1" : "0")
                + " | " + getDescription()
                + " | " + DateUtil.toFileString(by);
    }

    /**
     * Formats this deadline for display.
     *
     * @return Display representation of this deadline.
     */
    @Override
    public String toString() {
        return "[" + TaskType.DEADLINE.getCode() + "] "
                + super.toString()
                + " (by: "
                + DateUtil.toDisplayString(getBy()) + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Deadline other)) {
            return false;
        }
        return getDescription().equalsIgnoreCase(other.getDescription())
                && getBy().equals(other.getBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getDescription().toLowerCase(),
                getBy());
    }
}
