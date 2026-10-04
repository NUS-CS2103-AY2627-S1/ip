package shrek.task;

import java.util.Collection;

/**
 * Represents a task with no date or time attached to it.
 */
public class Todo extends Task {

    /**
     * Creates a new todo task.
     *
     * @param description the text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates a new todo task with tags.
     *
     * @param description the text describing the task.
     * @param tags        the initial tags for the task.
     */
    public Todo(String description, Collection<String> tags) {
        super(description, tags);
    }

    @Override
    public String toSaveFormat() {
        return "T | " + (isDone ? "1" : "0") + " | " + description + getTagsStorageSuffix();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
