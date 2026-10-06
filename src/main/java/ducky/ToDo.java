package ducky;

import java.util.List;

/**
 * Represents a task without a date attached to it.
 */
public class ToDo extends Task {
    /**
     * Creates a to-do task with the given description.
     *
     * @param description the description of the task.
     */
    public ToDo(String description) {
        this(description, List.of());
    }

    /**
     * Creates a to-do task with the given description and tags.
     *
     * @param description the description of the task.
     * @param tags the tags associated with the task.
     */
    public ToDo(String description, List<String> tags) {
        super(description, tags);
    }

    /**
     * Returns this to-do task in Ducky's display format.
     *
     * @return the formatted to-do task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
