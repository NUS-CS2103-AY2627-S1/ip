package milo.task;

import java.time.LocalDateTime;

import milo.parser.DateTimeParser;

/** A task that must be completed by a particular date and time. */
public class Deadline extends Task {
    /** Date and time by which the task should be completed. */
    private final LocalDateTime deadlineDate;

    /** Creates a deadline from a user-entered date/time. */
    public Deadline(String description, String deadlineDate) {
        super(description);
        this.deadlineDate = DateTimeParser.parse(deadlineDate);
    }

    /** Creates a deadline from an already parsed date/time. */
    public Deadline(String description, LocalDateTime deadlineDate) {
        super(description);
        this.deadlineDate = deadlineDate;
    }

    /** Returns this deadline's date and time. */
    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    /** Returns this deadline in the format used by storage. */
    @Override
    public String storageString() {
        return String.format("D | %s | %s", super.storageString(),
                DateTimeParser.formatForStorage(deadlineDate));
    }

    /** Returns this deadline in the format shown to the user. */
    @Override
    public String toString() {
        return String.format("[D] %s (by: %s)", super.toString(),
                DateTimeParser.formatForDisplay(deadlineDate));
    }
}
