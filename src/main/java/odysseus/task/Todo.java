package odysseus.task;

/** A to_do: a task with no date/time attached. */
public class Todo extends Task {

    /**
     * Constructs a todo task.
     *
     * @param description string description of the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     *
     * @return the todo encoded as a save-file line
     */
    @Override
    public String toSaveFormat() {
        return String.format("%s | %d | %s",
                "T",
                this.isDone ? 1 : 0,
                this.description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
