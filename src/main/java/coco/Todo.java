package coco;

/**
 * A task with just a description and no date/time attached.
 */
public class Todo extends Task {
    /**
     * Creates a new, not-yet-done todo.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toSaveFormat() {
        return "T | " + super.toSaveFormat();
    }
}
