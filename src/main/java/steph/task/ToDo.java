package steph.task;

/**
 * A task with only a name -- no date or time attached.
 */
public class ToDo extends Task {

    /**
     * Constructs a to-do with the given name.
     *
     * @param name Description of the task.
     */
    public ToDo(String name) {
        super(name);
    }

    /**
     * Returns the full save-file line for this to-do, e.g.
     * {@code "T | 0 | read book"}.
     */
    @Override
    public String toFileFormat() {
        return "T" + super.toFileFormat();
    }

    /** Returns this to-do as {@code "[T][ ] name"} (or {@code "[X]"} when done). */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
