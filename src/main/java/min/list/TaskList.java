package min.list;

import java.util.List;

import min.task.Task;

/**
 * Stores and updates Min's tasks.
 */
public class TaskList extends ItemList<Task> {
    /**
     * Creates a task list whose initial displayed view contains every task.
     *
     * @param tasks The initial tasks.
     */
    public TaskList(List<Task> tasks) {
        super(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void addTask(Task task) {
        add(task);
    }

    /**
     * Sets the displayed view to tasks whose descriptions contain the given keyword.
     *
     * @param keyword The text to match against task descriptions.
     * @return A read-only list of matching tasks.
     */
    public List<Task> showMatchingTasks(String keyword) {
        assert keyword != null && !keyword.isBlank()
                : "Find keyword must not be blank.";

        return showMatching(task -> task.getDescription().contains(keyword));
    }

    /** Restores and returns the complete task list as the displayed view. */
    public List<Task> showAllTasks() {
        return showAll();
    }

    /**
     * Marks the displayed task at the given index as complete.
     *
     * @param index The zero-based index in the displayed task view.
     * @return The marked task.
     */
    public Task markTask(int index) {
        Task task = getDisplayedItem(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the displayed task at the given index as incomplete.
     *
     * @param index The zero-based index in the displayed task view.
     * @return The unmarked task.
     */
    public Task unmarkTask(int index) {
        Task task = getDisplayedItem(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Removes and returns the displayed task at the given index.
     *
     * @param index The zero-based index in the displayed task view.
     * @return The removed task.
     */
    public Task deleteTask(int index) {
        return delete(index);
    }

    /** Returns the number of tasks in the current displayed view. */
    public int getDisplayedTaskCount() {
        return getDisplayedCount();
    }

    /** Returns a read-only snapshot of the current tasks. */
    public List<Task> getTasks() {
        return getItems();
    }
}
