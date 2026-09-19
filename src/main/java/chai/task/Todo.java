package chai.task;

/** A task without a date or time attached to it. */
public class Todo extends Task {

    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description Text describing the todo.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
