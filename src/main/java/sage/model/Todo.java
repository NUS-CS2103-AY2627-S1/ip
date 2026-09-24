package sage.model;

/** A task without a deadline or scheduled time. */
public class Todo extends Task {

    /** Creates a todo with the supplied description.
     *
     * @param description todo description
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns this todo in Sage's display format. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
