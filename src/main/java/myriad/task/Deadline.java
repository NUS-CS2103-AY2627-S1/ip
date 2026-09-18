package myriad.task;

import java.time.LocalDateTime;

/**
 * A task that has to be done by one date, optionally with a time of day.
 * The due date is kept as a {@link TaskDateTime} rather than raw text, so
 * that {@code show} can compare it against a queried date/time instead of
 * matching strings.
 */
public class Deadline extends Task {

    /** When the task is due. */
    private final TaskDateTime by;

    /**
     * Creates a not-done {@code Deadline}.
     *
     * @param description what the task is.
     * @param by          when it is due, already parsed.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        assert by != null : "TaskDateTime.parse never returns null";

        this.by = by;
    }

    @Override
    public TaskType getType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns whether {@code other} is a deadline with the same description
     * and the same due date. See {@link Task#hasSameDetails}.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        // Same type is checked first, so the cast below cannot fail.
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    /**
     * Returns this task's save-format line prefixed with its type code and
     * suffixed with the raw due date, so both the type and the date can be
     * recovered when the data file is read back in.
     */
    @Override
    public String toSaveFormat() {
        return String.format("%s | %s | %s", getType().getCode(), super.toSaveFormat(), by.toSaveFormat());
    }

    /**
     * Returns the display form, e.g.
     * {@code [D][ ] return book (by: Dec 02 2019)}, with the date shown in
     * {@link TaskDateTime}'s display format, not the save format.
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), by);
    }

    /**
     * Matches if the period from {@code from} to {@code to} overlaps this
     * deadline's own instant (or, if the deadline has no time of its own, its
     * whole day). See {@link TaskDateTime#rangesOverlap} for the general rule.
     */
    @Override
    public boolean overlaps(LocalDateTime from, LocalDateTime to) {
        return TaskDateTime.rangesOverlap(
                by.rangeStart(), by.rangeEnd(), from, to);
    }
}
