package milo.task;

/**
 * Represents a task with no date or time attached to it,
 * for example "visit new theme park".
 */
public class Todo extends Task {
    /**
     * Creates a todo.
     *
     * @param description what the task is about
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns an independent copy of this todo.
     *
     * @return a todo equal to this one but sharing no state with it
     */
    @Override
    public Todo copy() {
        Todo copy = new Todo(description);
        copy.isDone = this.isDone;
        return copy;
    }

    /**
     * Returns the todo tagged with its type, for example {@code [T][ ] borrow book}.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns this task as one line of the save file.
     */
    @Override
    public String toFileFormat() {
        return "T" + FILE_SEPARATOR + super.toFileFormat();
    }
}
