
package ip.model;

import ip.parser.ParsedDateTime;

/** Represents a task that must be completed by a specified date or time. */
public class Deadline extends Task {

    protected ParsedDateTime by;

    /** Creates a deadline with a description and due date or time. */
    public Deadline(String description, ParsedDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    /** Returns the serialized deadline representation. */
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by.toFileFormat();
    }

    @Override
    /** Returns the user-facing deadline representation. */
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
