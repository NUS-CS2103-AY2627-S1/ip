package com.benthecat.kachow.task;

import java.time.LocalDate;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description Text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates a todo task with a restored completion status.
     *
     * @param description Text describing the task.
     * @param isDone Whether the task has been completed.
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    /** {@inheritDoc} */
    @Override
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String getStatusText() {
        return "[T]" + super.getStatusText();
    }
}
