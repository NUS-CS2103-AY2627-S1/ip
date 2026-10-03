package ramly.model;

/** Represents a task without a deadline or event period. */
public class Todo extends Task {

    protected String by;


    /** Creates a todo task with the specified description. */
    public Todo(String description) {
        super(description, TaskType.TODO);

    }

    /** Returns the user-facing todo representation. */
    @Override
    public String toString() {
        return super.toString();
    }
}
