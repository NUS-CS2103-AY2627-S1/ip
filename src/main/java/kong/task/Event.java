package kong.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a task that occurs over an inclusive date range.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private LocalDate from;
    private LocalDate to;

    /**
     * Creates an incomplete event.
     *
     * @param desc non-blank task description validated by the caller
     * @param from non-null first date validated by the caller
     * @param to non-null last date validated by the caller
     */
    public Event(String desc, LocalDate from, LocalDate to) {
        this(desc, false, from, to);
    }

    /**
     * Creates an event with an explicit completion status.
     *
     * @param desc non-blank task description validated by the caller
     * @param done whether the event is complete
     * @param from non-null first date validated by the caller
     * @param to non-null last date validated by the caller
     */
    public Event(String desc, boolean done, LocalDate from, LocalDate to) {
        super(desc, done);
        assert from != null : "An event start date must be validated before task creation";
        assert to != null : "An event end date must be validated before task creation";
        assert !from.isAfter(to) : "An event start date cannot be after the end date";
        this.from = from;
        this.to = to;
    }

    /** {@inheritDoc} */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(this.from) && !date.isAfter(this.to);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<LocalDate> getSortDate() {
        return Optional.of(this.from);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSameTask(Task other) {
        if (!super.isSameTask(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return this.from.equals(otherEvent.from) && this.to.equals(otherEvent.to);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        Event other = (Event) obj;
        return Objects.equals(this.from, other.from) && Objects.equals(this.to, other.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.from, this.to);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)",
                super.toString(), this.from.format(DISPLAY_DATE_FORMAT), this.to.format(DISPLAY_DATE_FORMAT));
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return String.format("E | %s | %s | %s | %s",
                this.getDoneStatus(), this.getDescription(), this.from, this.to);
    }
}
