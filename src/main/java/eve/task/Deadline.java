package eve.task;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/** A task that needs to be done before a specific date. */
public class Deadline extends Task {
    protected LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description what the task is.
     * @param by the date it is due.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /** Returns this task's display text, prefixed with "[D]" and suffixed with its due date. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toSaveFormat() {
        return "D | " + super.toSaveFormat() + " | " + by;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /** Returns this deadline's due date, so it sorts by when it's due. */
    @Override
    public Optional<LocalDate> getScheduleDate() {
        return Optional.of(by);
    }

    /** Also compares the due date, on top of the type/description check {@link Task#equals} does. */
    @Override
    public boolean equals(Object other) {
        return super.equals(other) && by.equals(((Deadline) other).by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), by);
    }
}
