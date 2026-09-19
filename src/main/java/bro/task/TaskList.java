package bro.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents and manages the list of tasks in the Bro application.
 * Provides operations to add, delete, retrieve, and filter tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList initialized with an existing list of tasks.
     *
     * @param tasks An ArrayList of Task objects.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert task != null : "Cannot add a null task to TaskList";

        int previousSize = this.tasks.size();
        this.tasks.add(task);

        assert this.tasks.size() == previousSize + 1 : "TaskList size should increment by 1 after add";
    }

    /**
     * Removes and returns the task at the specified 0-based index.
     *
     * @param index The 0-based index of the task to delete.
     * @return The removed Task.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public Task delete(int index) {
        int previousSize = this.tasks.size();
        Task removed = this.tasks.remove(index);

        assert removed != null : "Deleted task should not be null";
        assert this.tasks.size() == previousSize - 1 : "TaskList size should decrement by 1 after delete";

        return removed;
    }

    /**
     * Retrieves the task at the specified 0-based index.
     *
     * @param index The 0-based index of the task.
     * @return The Task at the specified index.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public Task get(int index) {
        int previousSize = this.tasks.size();
        Task task = this.tasks.get(index);

        assert this.tasks.size() == previousSize : "TaskList size should not change after get";

        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The total number of tasks.
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
     * Finds and returns all tasks occurring on or involving the specified date.
     *
     * @param date The target date to search for.
     * @return An ArrayList of matching Task objects.
     */
    public ArrayList<Task> findTasksOnDate(LocalDate date) {
        return this.tasks.stream()
                .filter(task -> task.isOnDate(date))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Finds and returns all tasks which contain any of the string keywords (case-insensitive).
     *
     * @param keywords One or more keywords or keyword phrases to search for.
     * @return An ArrayList of matching Task objects.
     */
    public ArrayList<Task> findTasksByKeywords(String... keywords) {
        if (keywords == null || keywords.length == 0) {
            return new ArrayList<>();
        }

        return this.tasks.stream()
                .filter(task -> task.containsKeywords(keywords))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Checks if an equivalent task already exists in the task list.
     *
     * @param task The task to check for duplicates.
     * @return True if a duplicate exists, false otherwise.
     */
    public boolean hasDuplicate(Task task) {
        if (task == null) {
            return false;
        }
        return this.tasks.stream().anyMatch(existing -> existing.equals(task));
    }

    /**
     * Returns the existing duplicate task from the list, or null if none exists.
     *
     * @param task The task to find duplicates for.
     * @return The existing duplicate Task, or null.
     */
    public Task getDuplicate(Task task) {
        if (task == null) {
            return null;
        }
        return this.tasks.stream()
                .filter(existing -> existing.equals(task))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return An ArrayList containing all tasks.
     */
    public ArrayList<Task> getAllTasks() {
        return this.tasks;
    }
}
