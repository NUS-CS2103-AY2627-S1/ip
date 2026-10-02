package dave.task;

import java.util.ArrayList;

import dave.exception.DaveCommandException;

/**
 * Represents the list of tasks managed by the chatbot.
 * Provides operations to add, delete, access, and update tasks.
 */
public class TaskList {

    /** Internal list storing the tasks. */
    private final ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList initialized with the specified list of tasks.
     *
     * @param tasks Initial list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the task list.
     *
     * @param task Task to be added.
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Checks whether an identical task already exists in the list.
     *
     * @param task Task to check for duplication.
     * @return True if a matching task already exists, false otherwise.
     */
    public boolean hasDuplicate(Task task) {
        return findDuplicate(task) != null;
    }

    /**
     * Finds and returns the first existing task that is identical to the given task.
     *
     * @param task Task to match against.
     * @return Matching existing task, or null if none is found.
     */
    public Task findDuplicate(Task task) {
        if (task == null) {
            return null;
        }
        for (Task existing : this.tasks) {
            if (existing.isSameTask(task)) {
                return existing;
            }
        }
        return null;
    }

    /**
     * Deletes and returns the task at the specified 0-based index.
     *
     * @param index 0-based index of the task to delete.
     * @return Task that was removed.
     * @throws DaveCommandException If the index is out of bounds.
     */
    public Task delete(int index) {
        if (index < 0 || index >= this.tasks.size()) {
            throw new DaveCommandException("Pardon, Sire! That is an invalid scroll number.");
        }
        return this.tasks.remove(index);
    }

    /**
     * Returns the task at the specified 0-based index.
     *
     * @param index 0-based index of the task to retrieve.
     * @return Task at the specified index.
     * @throws DaveCommandException If the index is out of bounds.
     */
    public Task get(int index) {
        if (index < 0 || index >= this.tasks.size()) {
            throw new DaveCommandException("Pardon, Sire! That is an invalid scroll number.");
        }
        return this.tasks.get(index);
    }

    /**
     * Updates the completion status of the task at the specified 0-based index.
     *
     * @param index 0-based index of the task to update.
     * @param isDone True to mark completed, false to mark uncompleted.
     * @return Updated task.
     * @throws DaveCommandException If the index is out of bounds.
     */
    public Task setDone(int index, boolean isDone) {
        if (index < 0 || index >= this.tasks.size()) {
            throw new DaveCommandException("Pardon, Sire! That is an invalid scroll number.");
        }
        Task task = this.tasks.get(index);
        task.setDone(isDone);
        return task;
    }

    /**
     * Finds and returns tasks whose descriptions contain the specified keyword,
     * retaining each task's 1-based index in the task list.
     *
     * @param keyword Keyword to search for within task descriptions.
     * @return List of matching tasks with their original 1-based indices.
     */
    public ArrayList<IndexedTask> find(String keyword) {
        ArrayList<IndexedTask> matchingTasks = new ArrayList<>();
        for (int i = 0; i < this.tasks.size(); i++) {
            Task task = this.tasks.get(i);
            if (task.getDescription().contains(keyword)) {
                matchingTasks.add(new IndexedTask(i + 1, task));
            }
        }
        return matchingTasks;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns whether the task list is empty.
     *
     * @return True if the list contains no tasks, false otherwise.
     */
    public boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    /**
     * Returns the internal list of tasks.
     *
     * @return Internal ArrayList of tasks.
     */
    public ArrayList<Task> asList() {
        return this.tasks;
    }
}
