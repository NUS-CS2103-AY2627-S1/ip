package noms.task;

/** Represents a task with only a description and no associated date. */
public class ToDo extends Task {

    /**
     * Creates a todo with the given description.
     *
     * @param description the text describing the task
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }
}
