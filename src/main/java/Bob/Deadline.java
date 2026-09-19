package bob;

import java.time.LocalDate;

/** A task that must be completed by a specified date. */
public class Deadline extends Task {
    private final LocalDate end;

    /**
     * Creates a deadline with a description and due date.
     *
     * @param item Deadline description.
     * @param end Deadline due date.
     */
    public Deadline(String item, LocalDate end) {
        super(item);
        if (end == null) {
            throw new IllegalArgumentException("A deadline must have a due date");
        }
        this.end = end;
    }

    /**
     * Returns the storage representation of this deadline.
     *
     * @return Storage line.
     */
    @Override
    public String toStorageString() {
        // LocalDate.toString() uses a stable ISO representation.
        return DEADLINE_TYPE + " | " + (isDone() ? COMPLETE_FLAG : "0") + " | "
                + getItem() + " | " + end;
    }

    /**
     * Returns the display representation of this deadline.
     *
     * @return Display form of this deadline.
     */
    @Override
    public String toString() {
        return "[D][" + (isDone() ? "X" : " ") + "] "
                + getItem() + " (by: " + end.format(DateFormats.DISPLAY_DATE) + ")";
    }
}
