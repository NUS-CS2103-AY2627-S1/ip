package pdd.task;

/** A task with no date/time attached, other than its description. */
public class Todo extends Task {
    /** Creates a not-done todo with the given description. */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /** Serializes this task to a single line in the save-file format. */
    public String toFileFormat() {
        return "T" + FIELD_SEPARATOR + getStatusValue() + FIELD_SEPARATOR + description;
    }
}
