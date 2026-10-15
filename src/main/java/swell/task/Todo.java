package swell.task;

import java.util.ArrayList;

/**
 * Represents a todo task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description task description.
     */
    public Todo(String description) {
        super(TaskType.TODO, description);
    }

    /**
     * Creates a todo task with the given description and tags.
     *
     * @param description task description.
     * @param tags task tags.
     */
    public Todo(String description, ArrayList<String> tags) {
        super(TaskType.TODO, description, tags);
    }
}
