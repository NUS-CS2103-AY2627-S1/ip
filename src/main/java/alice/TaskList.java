package alice;

import java.util.ArrayList;

/**
 * Wraps an ArrayList of Task objects and provides convenient operations
 * for manipulating the task list such as adding, removing, getting, and validation.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList initialized with the given list of tasks.
     *
     * @param tasks The initial list of tasks. If null, an empty list is created.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The size of the task list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks if the task list is empty.
     *
     * @return true if the list contains no tasks, false otherwise.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Retrieves the task at the specified index.
     *
     * @param index The 0-based index of the task.
     * @return The Task at the given index.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public Task get(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task index out of range");
        }
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert task != null : "Task list cannot contain a null task";
        tasks.add(task);
    }

    /**
     * Removes the task at the specified index.
     *
     * @param index The 0-based index of the task to remove.
     * @return The removed task.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public Task remove(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task index out of range");
        }
        return tasks.remove(index);
    }

    /**
     * Marks the task at the given index as done.
     *
     * @param index The 0-based index of the task to mark.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public void mark(int index) {
        get(index).toggleStatus();
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index The 0-based index of the task to unmark.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public void unmark(int index) {
        get(index).toggleStatus();
    }

    /**
     * Returns the underlying ArrayList of tasks.
     *
     * @return The ArrayList containing all tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Creates a deep copy of this task list for undo history.
     *
     * @return A task list containing independent copies of all tasks.
     */
    public TaskList copy() {
        ArrayList<Task> taskCopies = new ArrayList<>();
        for (Task task : tasks) {
            taskCopies.add(task.copy());
        }
        return new TaskList(taskCopies);
    }

    /**
     * Checks if a given 0-based index is within the bounds of the list.
     *
     * @param index The index to check.
     * @return true if the index is valid, false otherwise.
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Finds all tasks whose description contains the given keyword.
     *
     * @param keyword The search keyword (case-insensitive).
     * @return A new TaskList containing only matching tasks.
     */
    public TaskList find(String keyword) {
        TaskList matchingTasks = new TaskList();
        String lowerKeyword = keyword.toLowerCase();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowerKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
}
