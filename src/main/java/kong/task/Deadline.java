package kong.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a task that is due on a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param desc non-blank task description validated by the caller
     * @param by non-null due date validated by the caller
     */
    public Deadline(String desc, LocalDate by) {
        this(desc, false, by);
    }

    /**
     * Creates a deadline with an explicit completion status.
     *
     * @param desc non-blank task description validated by the caller
     * @param done whether the deadline is complete
     * @param by non-null due date validated by the caller
     */
    public Deadline(String desc, boolean done, LocalDate by) {
        super(desc, done);
        assert by != null : "A deadline date must be validated before task creation";
        this.by = by;
    }

    /** {@inheritDoc} */
    @Override
    public boolean occursOn(LocalDate date) {
        return this.by.equals(date);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<LocalDate> getSortDate() {
        return Optional.of(this.by);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSameTask(Task other) {
        if (!super.isSameTask(other)) {
            return false;
        }
        Deadline otherDeadline = (Deadline) other;
        return this.by.equals(otherDeadline.by);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        Deadline other = (Deadline) obj;
        return Objects.equals(this.by, other.by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.by);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), this.by.format(DISPLAY_DATE_FORMAT));
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return String.format("D | %s | %s | %s", this.getDoneStatus(), this.getDescription(), this.by);
    }
}
