package gunna;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import gunna.task.Deadline;
import gunna.task.Task;

/**
 * Represents a list of tasks.
 * Provides operations to add, delete, mark, and retrieve tasks.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a TaskList with the given list of tasks.
     *
     * @param tasks The initial list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Task list cannot be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert task != null : "Cannot add null task to list";
        tasks.add(task);
    }

    /**
     * Deletes a task at the specified index (0-based).
     *
     * @param index The index of the task to delete.
     * @return The deleted task.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks the task at the specified index as done.
     *
     * @param index The index of the task to mark.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public void mark(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at the specified index as not done.
     *
     * @param index The index of the task to unmark.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public void unmark(int index) {
        tasks.get(index).markAsNotDone();
    }

    /**
     * Gets the task at the specified index.
     *
     * @param index The index of the task to retrieve.
     * @return The task at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public Task get(int index) {
        return tasks.get(index);
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
     * Returns all tasks in the list.
     * Used primarily for saving to storage.
     *
     * @return The underlying ArrayList of tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Finds and returns all tasks that occur on a specific date.
     * Currently only checks Deadline tasks.
     *
     * @param date The date to search for.
     * @return A list of tasks occurring on the specified date.
     */
    public ArrayList<Task> getTasksOnDate(LocalDate date) {
        assert date != null : "Date cannot be null";
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                if (deadline.getByDate().equals(date)) {
                    matchingTasks.add(task);
                }
            }
        }
        return matchingTasks;
    }

    /**
     * Finds and returns all tasks whose descriptions contain the given keyword.
     * The search is case-insensitive.
     *
     * @param keyword The keyword to search for in task descriptions.
     * @return A list of tasks whose descriptions contain the keyword.
     */
    public ArrayList<Task> findTasksByKeyword(String keyword) {
        assert keyword != null : "Keyword cannot be null";


        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
