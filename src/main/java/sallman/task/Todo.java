package sallman.task;

/**
 * A task with no date or time attached to it, e.g. {@code visit new theme park}.
 */
public class Todo extends Task {

    /**
     * Creates a todo that is initially not done.
     *
     * @param description what the task involves
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns an independent copy of this todo.
     *
     * @return a todo equal to this one, sharing none of its mutable state
     */
    @Override
    public Task copy() {
        return copyInto(new Todo(description));
    }

    /**
     * Returns this todo as one line of the data file,
     * e.g. {@code T | 0 | borrow book}.
     */
    @Override
    public String toFileFormat() {
        return "T" + SEPARATOR + super.toFileFormat();
    }

    /**
     * Returns this todo as it should appear to the user,
     * e.g. {@code [T][ ] borrow book}.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
