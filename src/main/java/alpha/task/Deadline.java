package alpha.task;

import java.time.LocalDateTime;

/** A task that must be completed by a specified date or time. */
public class Deadline extends Task {
    private final LocalDateTime by;

    /** Creates a deadline by parsing the supplied date or time text. */
    public Deadline(String description, String by) {
        this(description, DateTimeParser.parse(by));
    }

    /** Creates a deadline with an already parsed date and time. */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "A deadline must have a parsed due date";
        this.by = by;
    }

    /** Returns the date and time by which this task should be completed. */
    public LocalDateTime getBy() {
        return this.by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    protected String getTimeDetails() {
        return String.format(" (by: %s)", DateTimeParser.format(this.by));
    }

    @Override
    protected LocalDateTime getSortDateTime() {
        return this.by;
    }
}
