package nimbus.task;

/**
 * Represents a Todo task in the Nimbus application.
 */
public class Todo extends Task {

    /**
     * Constructs a Todo object with a name
     *
     * @param name Name of the todo.
     */
    public Todo(String name) {
        super(name, TaskType.TODO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toFileString() {
        return super.toFileString();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
