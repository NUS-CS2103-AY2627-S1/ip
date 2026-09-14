package penguin.task;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stores an independent snapshot of a task list and its undo history.
 */
public final class TaskListSnapshot {
    /** Tasks captured in the snapshot. */
    private final List<Task> tasks;

    /** Undo states captured in the snapshot. */
    private final Deque<List<Task>> history;

    /**
     * Creates an independent snapshot from the supplied task state.
     *
     * @param currentTasks tasks to capture.
     * @param currentHistory undo history to capture.
     */
    TaskListSnapshot(List<Task> currentTasks,
            Deque<List<Task>> currentHistory) {
        this.tasks = copyTasks(currentTasks);
        this.history = copyHistory(currentHistory);
    }

    /**
     * Returns independent copies of the captured tasks.
     *
     * @return copied task list.
     */
    List<Task> copyTasks() {
        return copyTasks(tasks);
    }

    /**
     * Returns independent copies of the captured undo states.
     *
     * @return copied undo history.
     */
    Deque<List<Task>> copyHistory() {
        return copyHistory(history);
    }

    /**
     * Creates independent copies of a task collection.
     *
     * @param sourceTasks tasks to copy.
     * @return copied tasks in their original order.
     */
    private static List<Task> copyTasks(List<Task> sourceTasks) {
        return sourceTasks.stream()
                .map(Task::copy)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Creates independent copies of all states in an undo history.
     *
     * @param sourceHistory undo history to copy.
     * @return copied undo history in its original order.
     */
    private static Deque<List<Task>> copyHistory(Deque<List<Task>> sourceHistory) {
        Deque<List<Task>> copiedHistory = new ArrayDeque<>();
        for (List<Task> state : sourceHistory) {
            copiedHistory.addLast(copyTasks(state));
        }
        return copiedHistory;
    }
}
