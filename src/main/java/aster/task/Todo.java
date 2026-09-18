package aster.task;

/**
 * A task with no date attached, such as {@code read book}.
 *
 * <p>A todo adds only the type marker {@code [T]} to the display form
 * inherited from {@link Task}.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description, initially not done.
     *
     * @param description text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo as its type marker followed by the inherited status
     * and description, for example {@code [T][X] read book}.
     *
     * @return the display form of this todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
