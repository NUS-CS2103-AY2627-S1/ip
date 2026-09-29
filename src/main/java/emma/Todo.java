package emma;

/**
 * A task with no timing attached.
 */
public class Todo extends Task {

    /**
     * Creates a todo.
     *
     * @param description what the user typed.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }

    /* The toString of todo is the task's toString without append */
}
