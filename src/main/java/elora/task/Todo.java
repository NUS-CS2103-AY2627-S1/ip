package elora.task;

/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a new todo with the given description.
     *
     * @param description What needs to be done.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo's representation for the save file, as
     * "T | &lt;base task format&gt;".
     *
     * @return The save-file line for this todo.
     */
    @Override
    public String toSaveFormat() {
        return "T | " + super.toSaveFormat();
    }

    /**
     * Returns this todo as "[T]&lt;base task format&gt;".
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
