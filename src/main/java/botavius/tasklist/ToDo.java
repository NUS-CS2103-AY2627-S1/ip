package botavius.tasklist;

/** A task with a description and no deadline or event times. */
public class ToDo extends Task {

    /**
     * Creates an incomplete to-do task.
     *
     * @param description task description
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Formats the to-do task for display.
     *
     * @return task text prefixed with the to-do marker
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
    /** @return the to-do task in the format used when persisting it */
    public String toStorageString() {
        return "[T]" + super.toStorageString();
    }

}
