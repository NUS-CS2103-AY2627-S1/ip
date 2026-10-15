package org.cs2103t.marquee.cli.task;

import java.time.LocalDateTime;

import org.cs2103t.marquee.core.task.Task;

/**
 * Representation of a generic task with no starting or ending time.
 *
 * @see Task
 */
public final class TodoTask extends Task {
    /**
     * Creates a new {@code TodoTask} with the given description,
     * then mark it as either completed or incomplete.
     *
     * @param description the description of the task
     * @param isMarked whether the task has been completed or not
     */
    public TodoTask(String description, boolean isMarked) {
        super(description, isMarked, null, null, TaskTags.TODO_TAG);
    }

    /**
     * Creates a new {@code TodoTask} with the given description,
     * then mark it as incomplete.
     *
     * @param description the description of the task
     */
    public TodoTask(String description) {
        this(description, false);
    }

    private TodoTask() {
        super();
    }

    @Override
    public void setStart(LocalDateTime newStart) {
        if (newStart != null) {
            throw new UnsupportedOperationException("Todo task doesn't have a starting time");
        }
    }

    @Override
    public void setEnd(LocalDateTime newEnd) {
        if (newEnd != null) {
            throw new UnsupportedOperationException("Todo task doesn't have an ending time");
        }
    }
}
