package vega;

/** Represents a task without a date or time. */
public class Todo extends Task {
    /**
     * Creates a to-do task with the given description.
     *
     * @param description Description of the to-do task.
     */
    public Todo(String description) {
        super(description, "T");
    }
}
