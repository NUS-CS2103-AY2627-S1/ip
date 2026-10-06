package com.gihun456.model;

/**
 * Represents a task with no additional date information.
 */
public class Todo extends Task {
    /**
     * Creates a todo task.
     *
     * @param taskName Description of the todo item.
     */
    public Todo(String taskName) {
        super(taskName);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
