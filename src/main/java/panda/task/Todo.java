package panda.task;

/**
 * Represents a task with no date or time attached.
 *
 * Models to-dos as a Task subtype so they inherit their name,
 * completion status, and mark/unmark behavior.
 */
public class Todo extends Task {
    /**
     * Creates an unfinished to-do with the given description.
     *
     * @param name the description entered for the to-do.
     */
    public Todo(String name) {
        super(name);
    }

    /**
     * Returns the to-do task type.
     *
     * @return the to-do task type.
     */
    @Override
    public TaskType getType() {
        return TaskType.TODO;
    }
}
