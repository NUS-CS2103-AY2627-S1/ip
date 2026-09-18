package meowmeow.task;

/**
 * A plain task with no associated date or time, added via the "todo"
 * command. Renders with a "[T]" tag ahead of the usual status/description,
 * via {@link TaskType#TODO} - the base {@link Task#toString()} already
 * produces the full rendering, so no override is needed here.
 */
public class Todo extends Task {

    /**
     * Constructs a todo with the given description.
     *
     * @param description the task text as the user typed it.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
