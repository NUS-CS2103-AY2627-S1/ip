package bott.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the list of tasks Bott is tracking, and the operations
 * available on it. Task positions are 1-based, matching how the user
 * refers to tasks (e.g. "mark 2").
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Creates a task list containing the given tasks.
     *
     * @param tasks Tasks to start with.
     */
    public TaskList(List<Task> tasks) {
        // Storage.load() is the only production caller and always returns a
        // list (empty when there is no save file), never null; a null here
        // would be an internal contract violation, not a user error.
        assert tasks != null : "task list must not be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given position.
     *
     * @param taskNumber 1-based position of the task to remove.
     * @return Removed task.
     */
    public Task remove(int taskNumber) {
        // Callers pass a position already validated by Parser.parseTaskNumber,
        // so it must be within 1..size here; an out-of-range value means a
        // caller skipped that check (a bug), not that the user mistyped.
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "task position out of range: " + taskNumber;
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the task at the given position.
     *
     * @param taskNumber 1-based position of the task.
     * @return Task at that position.
     */
    public Task get(int taskNumber) {
        // Same precondition as remove(): the position is expected to have
        // been validated already, so it must map to an existing task.
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "task position out of range: " + taskNumber;
        return tasks.get(taskNumber - 1);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns the tasks in the list, in order. */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks, in list order.
     */
    public List<Task> find(String keyword) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.matchesKeyword(keyword)) {
                matches.add(task);
            }
        }
        return matches;
    }
}
