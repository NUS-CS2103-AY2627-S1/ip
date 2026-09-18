package meowmeow.task;

import java.time.LocalDate;

/**
 * A task due by a given point, added via the "deadline" command. The "by"
 * value is a real {@link TaskDateTime}, so Meowmeow understands it as an
 * actual date (and optional time) rather than plain text - it is parsed on
 * input, re-formatted for display, and can be matched against a date by
 * "list &lt;date&gt;". Renders with a "[D]" tag (via {@link TaskType#DEADLINE})
 * and the due date in parentheses.
 */
public class Deadline extends Task {

    protected TaskDateTime by;

    /**
     * Constructs a deadline with the given description and due point.
     *
     * @param description the task text as the user typed it.
     * @param by          the date (and optional time) the task is due by.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description, TaskType.DEADLINE);
        // Both callers (Parser and Storage) parse the "/by" value into a real
        // TaskDateTime before constructing the deadline, so it is never null.
        assert by != null : "a deadline must have a due date";
        this.by = by;
    }

    /** Returns {@code true} when this deadline falls on {@code date}. */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.getDate().equals(date);
    }

    @Override
    protected String details() {
        return " (by: " + by + ")";
    }

    @Override
    protected String fileDetails() {
        return " | " + by.toFileString();
    }
}
