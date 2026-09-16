package turtley.model;

import java.util.Collection;

import turtley.exception.TurtleyException;

/**
 * A task without a deadline or event time range.
 */
public class ToDo extends Task {

    /**
     * Creates an unfinished to-do task.
     *
     * @param description the task description.
     */
    public ToDo(String description) {
        super(TaskType.TODO, requireDescription(description));
    }

    /**
     * Creates an unfinished to-do task with tags.
     *
     * @param description the task description.
     * @param tags the task tags.
     */
    public ToDo(String description, Collection<String> tags) {
        super(TaskType.TODO, requireDescription(description), tags);
    }

    /**
     * Ensures that a to-do has a meaningful name before it is created.
     *
     * @param description the proposed task description.
     * @return the unchanged non-blank description.
     * @throws TurtleyException if the description is missing or blank.
     */
    private static String requireDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new TurtleyException("Invalid format. Use: todo <description> [/tag #tag1 #tag2 ...]");
        }
        return description;
    }
}
