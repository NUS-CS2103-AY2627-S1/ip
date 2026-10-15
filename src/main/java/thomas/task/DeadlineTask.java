package thomas.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A task that must be done before a given date and time.
 * <p>
 * The due date is kept as a {@link LocalDateTime} rather than as the text the
 * user typed, so it is a real point in time the program understands: it is read
 * in as {@code yyyy-mm-dd HHmm} and shown back in a friendlier form. Both
 * formats are {@link Task}'s, shared with {@link EventTask}.
 */
public class DeadlineTask extends Task {
    /** When the task is due, as given after {@code /by}. */
    protected LocalDateTime by;

    /**
     * Creates a deadline that is not yet done.
     *
     * @param description The task text, without the {@code deadline} keyword.
     * @param by The date and time the task is due.
     */
    public DeadlineTask(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline with its type tag and due date.
     *
     * @return For example {@code "[D][ ] return book (by: Dec 02 2019, 6:00 PM)"}.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DATE_DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns whether this deadline falls due on a given day.
     * <p>
     * The time of day is dropped for the comparison, so a deadline at any hour
     * counts as being on that day.
     *
     * @param day The day being asked about.
     * @return True if the task is due on that day.
     */
    @Override
    public boolean occursOn(LocalDate day) {
        return by.toLocalDate().equals(day);
    }

    /**
     * Returns whether another object is this deadline written a second time:
     * the same description, as {@link Task#equals(Object)} checks, due at the
     * same moment.
     *
     * @param other The object to compare with.
     * @return True if {@code other} is a deadline with the same description and
     *         due date.
     */
    @Override
    public boolean equals(Object other) {
        return super.equals(other) && by.equals(((DeadlineTask) other).by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), by);
    }

    /**
     * Returns the deadline encoded for the save file, tagged {@code D} with the
     * due date as a fourth field.
     * <p>
     * The date is written in the format it is typed in, so the save file holds
     * nothing the parser cannot read back.
     *
     * @return For example {@code "D | 0 | return book | 2019-12-02 1800"}.
     */
    @Override
    public String toSaveFormat() {
        return "D" + FIELD_SEPARATOR + super.toSaveFormat()
                + FIELD_SEPARATOR + by.format(DATE_INPUT_FORMAT);
    }
}
