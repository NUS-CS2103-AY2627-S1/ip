package murphy.task;

/** A task without an associated date or time. */
public class Todo extends Task {
    /** Creates an incomplete todo task. */
    public Todo(String description) {
        super(description);
    }
}
