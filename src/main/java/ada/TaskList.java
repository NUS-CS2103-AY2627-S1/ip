package ada;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Maintains the in‑memory collection of tasks.
 * Provides operations to add, remove, retrieve and update task completion status.
 * Returns a copy of internal task list when exporting all tasks to prevent external modification.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs a TaskList using an existing list of initial tasks.
     *
     * @param initialTasks list of tasks loaded from storage
     */
    public TaskList(ArrayList<Task> initialTasks) {
        assert initialTasks != null : "A task list must have a backing collection";
        this.tasks = initialTasks;
    }

    /**
     * Appends a new task to the task collection.
     *
     * @param task the task object to be added
     */
    public void addTask(Task task) {
        assert task != null : "The task list must not contain null tasks";

        int oldSize = tasks.size();
        tasks.add(task);

        assert tasks.size() == oldSize + 1 : "Adding one task must increase the list size by one";
        assert tasks.get(oldSize) == task : "The added task must be stored at the end of the list";
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index zero‑based position of target task
     * @return the removed Task instance
     */
    public Task removeTask(int index) {
        assert index >= 0 && index < tasks.size() : "The index passed to removeTask must identify a task";

        int oldSize = tasks.size();
        Task expected = tasks.get(index);
        Task removed = tasks.remove(index);

        assert removed == expected;
        assert tasks.size() == oldSize - 1;

        return removed;
    }

    /**
     * Retrieves the task stored at given index.
     *
     * @param index zero‑based position of target task
     * @return the Task at that position
     */
    public Task getTask(int index) {
        assert index >= 0 && index < tasks.size();
        return tasks.get(index);
    }

    /**
     * Gets total number of tasks stored.
     *
     * @return count of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Marks the task at given index as completed.
     *
     * @param index zero‑based position of target task
     */
    public void markDone(int index) {
        tasks.get(index).markDone();
    }

    /**
     * Marks the task at given index as unfinished.
     *
     * @param index zero‑based position of target task
     */
    public void markUndone(int index) {
        tasks.get(index).markUndone();
    }

    /**
     * Sorts the internal task list according to the custom sort rules.
     */
    public void sortTasks() {
        tasks.sort(new TaskComparator());
    }

    /**
     * Returns a brand‑new copy of the internal task list.
     * External code cannot modify the original stored collection through this returned list.
     *
     * @return copied ArrayList containing all tasks
     */
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns list of tasks whose description contains the given keyword (case‑insensitive).
     *
     * @param keyword search keyword from find command
     * @return matched task list
     */
    public List<Task> matchTasksByKeyword(String keyword) {
        assert keyword != null : "A keyword is required for task searching";
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(lowerKeyword))
                .toList();
    }
}
