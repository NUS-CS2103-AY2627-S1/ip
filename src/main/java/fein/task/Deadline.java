package fein.task;

import java.time.LocalDateTime;

import fein.DateTimeParser;

/** A task that must be completed before a specified date or time. */
public class Deadline extends Task {
    /** The parsed deadline, when the user supplied a supported date and time. */
    private final LocalDateTime by;

    /** The original text for natural-language deadlines that cannot be parsed. */
    private final String originalBy;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, String by) {
        super(description);
        // Parser and storage reject blank due-date text before constructing a deadline.
        assert by != null && !by.isBlank() : "A deadline must have non-blank due-date text";
        this.by = DateTimeParser.parse(by);
        this.originalBy = by;
    }

    /** Returns the parsed deadline, or {@code null} for natural-language text. */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns the deadline in the form that should be displayed and saved. */
    public String getByText() {
        return by == null ? originalBy : DateTimeParser.format(by);
    }

    /** Returns whether another deadline has the same description and due date as this deadline. */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        if (!(other instanceof Deadline deadline)) {
            return false;
        }
        return super.hasSameDetailsAs(deadline)
                && getByText().equalsIgnoreCase(deadline.getByText());
    }

    /** Returns this deadline with its type and deadline markers. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + getByText() + ")";
    }
}
