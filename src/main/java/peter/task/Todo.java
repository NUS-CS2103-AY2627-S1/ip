package peter.task;

import java.util.List;

/**
 * Represents a task with no date attached to it.
 *
 * <p>Being unscheduled is what distinguishes a todo from the other task
 * types: it displays no schedule and never matches an {@code on <date>}
 * search, for which it relies on {@link Task#occursOn} returning
 * {@code false} by default.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     *
     * @param description what the task says.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    /**
     * Returns an empty string, since a todo has no schedule to display.
     */
    @Override
    public String getScheduleDetails() {
        return "";
    }

    /**
     * {@inheritDoc}
     *
     * <p>Returns no fields, since a todo has no schedule. Its line therefore
     * holds only the type, status, and description.
     */
    @Override
    protected List<String> getScheduleDataFields() {
        return List.of();
    }
}
