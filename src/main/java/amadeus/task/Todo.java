package amadeus.task;

/**
 * A task with nothing but a description.
 * <p>
 * The simplest of the three task types: it carries no date, so it never turns up
 * in the "on" command. It adds no state of its own to {@link Task} and exists to
 * give the list a task type that is just a thing to do.
 */
public class Todo extends Task {

    /**
     * Creates a todo that starts off not done.
     *
     * @param description what has to be done, e.g. "read book".
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo as the user sees it, e.g. "[T][ ] read book".
     * The "[T]" tag is what tells the three task types apart in a printed list.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
