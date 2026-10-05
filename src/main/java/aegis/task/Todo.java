package aegis.task;

/**
 * Represents a Todo type task in the Aegis task list.
 */
public class Todo extends Task {

    /**
     * Constructs a Todo task.
     *
     * @param description Description of the task.
     * @param isDone Whether the task has been marked as done.
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }

    @Override
    public String getFileSaveFormat() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

}
