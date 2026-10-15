package org.cs2103t.marquee.cli.task;

import org.cs2103t.marquee.core.task.TaskTag;

/**
 * Data class for task tags used by {@code TodoTask}, {@code DeadlineTask} and {@code EventTask}.
 *
 * @see TodoTask
 * @see DeadlineTask
 * @see EventTask
 */
public class TaskTags {
    /** Tag for a to-do task */
    public static final TaskTag TODO_TAG = TaskTag.createOrGet("T");
    /** Tag for a task with deadline */
    public static final TaskTag DEADLINE_TAG = TaskTag.createOrGet("D");
    /** Tag for an event */
    public static final TaskTag EVENT_TAG = TaskTag.createOrGet("E");

    // prevent instantiation
    private TaskTags() {}
}
