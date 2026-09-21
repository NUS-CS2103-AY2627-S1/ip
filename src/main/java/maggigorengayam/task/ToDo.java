package maggigorengayam.task;

/** A task with no date - just a description and a done/not-done status. */
public class ToDo extends Task {

    /** Creates a to-do with the given description. */
    public ToDo(String description) {
        super(description);
    }

    /** e.g. "[T][ ] read book". */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /** e.g. "T | 0 | read book". */
    @Override
    public String toSaveFormat() {
        return "T | " + getStatusFlag() + " | " + description;
    }
}
