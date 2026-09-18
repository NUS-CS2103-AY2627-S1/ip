package com.fireinyu.themyth.tasks;

import java.util.Comparator;
import java.util.List;

/**
 * Defines supported task ordering strategies.
 */
public enum TaskOrder {
    /** Sorts tasks by creation time. */
    CREATED(Comparator.comparing(Task::getCreated).reversed()),
    /** Sorts tasks by modification time. */
    MODIFIED(Comparator.comparing(Task::getLastModified).reversed()),
    /** Sorts tasks by description. */
    NAME(Comparator.comparing(Task::getDescription)),
    /** List unmarked tasks first. */
    PENDING(Comparator.comparing(Task::isCompleted));

    private final Comparator<Task> comparator;

    TaskOrder(Comparator<Task> comparator) {
        this.comparator = comparator;
    }

    /**
     * Sorts the supplied tasks using this ordering.
     *
     * @param tasks tasks to sort.
     */
    public void apply(List<Task> tasks) {
        tasks.sort(comparator);
    }
}
