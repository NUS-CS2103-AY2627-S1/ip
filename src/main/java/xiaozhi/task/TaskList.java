package xiaozhi.task;

import java.util.ArrayList;

/**
 * Holds XiaoZhi's current list of tasks.
 * <p>
 * A thin wrapper around an {@link ArrayList} of {@link Task}, kept so that
 * {@link xiaozhi.command.Command} and {@link xiaozhi.ui.Ui} depend on a
 * task list rather than a raw collection type.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list wrapping an existing list of tasks, e.g. one just loaded from disk.
     *
     * @param tasks The tasks to start with.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert task != null : "Cannot add a null task; every Command builds a real Task before calling this.";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given position.
     *
     * @param index 0-based position of the task to remove.
     * @return The task that was removed.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size()
                : "Index should already be validated by the caller (e.g. DeleteCommand) before reaching here.";
        return tasks.remove(index);
    }

    /**
     * Inserts a task at the given position, shifting the task currently at
     * that position (and everything after it) up by one.
     * <p>
     * Used by {@link xiaozhi.command.DeleteCommand#undo} to put a deleted
     * task back exactly where it was, rather than at the end of the list.
     *
     * @param index 0-based position to insert the task at.
     * @param task The task to insert.
     */
    public void insert(int index, Task task) {
        assert index >= 0 && index <= tasks.size()
                : "Index should already be validated by the caller (e.g. DeleteCommand.undo).";
        assert task != null : "Cannot insert a null task.";
        tasks.add(index, task);
    }

    /**
     * Returns the task at the given position, without removing it.
     *
     * @param index 0-based position of the task.
     * @return The task at that position.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "Index should already be validated by the caller (e.g. MarkCommand) before reaching here.";
        return tasks.get(index);
    }

    /**
     * Returns how many tasks are currently in the list.
     *
     * @return The number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of tasks, e.g. for {@link xiaozhi.storage.Storage} to save.
     *
     * @return The tasks, as a plain list.
     */
    public ArrayList<Task> asList() {
        return tasks;
    }

    /**
     * Finds every task whose description contains the given keyword.
     * The match is case-insensitive.
     *
     * @param keyword The keyword to search task descriptions for.
     * @return The matching tasks, in their original order. Empty if none match.
     */
    public ArrayList<Task> find(String keyword) {
        assert keyword != null : "Search keyword should never be null; Parser rejects a blank/missing keyword.";
        ArrayList<Task> matches = new ArrayList<>();
        String needle = keyword.toLowerCase();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(needle)) {
                matches.add(task);
            }
        }
        return matches;
    }
}
