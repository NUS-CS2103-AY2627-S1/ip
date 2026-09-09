package trayce.task;

import java.util.ArrayList;
import java.util.List;

/** Stores and manages the tasks currently known to Trayce. */
public class TaskList {
    private static final int MAX_TASKS = 100;
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing previously saved tasks.
     *
     * @param savedTasks tasks read from storage
     */
    public TaskList(List<Task> savedTasks) {
        tasks = new ArrayList<>(savedTasks.subList(0, Math.min(savedTasks.size(), MAX_TASKS)));
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to be added
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index, or {@code null} if invalid.
     *
     * @param taskIndex the zero-based index of the task to retrieve
     * @return the task at the specified index, or {@code null} if the index is out of bounds
     */
    public Task get(int taskIndex) {
        return taskIndex >= 0 && taskIndex < tasks.size() ? tasks.get(taskIndex) : null;
    }

    /**
     * Removes and returns the task at a zero-based index, or {@code null} if invalid.
     *
     * @param taskIndex the zero-based index of the task to delete
     * @return the deleted task, or {@code null} if the index is out of bounds
     */
    public Task delete(int taskIndex) {
        return taskIndex >= 0 && taskIndex < tasks.size() ? tasks.remove(taskIndex) : null;
    }

    /**
     * Returns a read-only snapshot of the current tasks.
     *
     * @return an unmodifiable list containing all current tasks
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the current number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has reached its supported capacity.
     *
     * @return {@code true} if the list is full, otherwise {@code false}
     */
    public boolean isFull() {
        return tasks.size() >= MAX_TASKS;
    }

    /** Returns tasks whose descriptions contain the given keyword. */
    public List<Task> find(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerCaseKeyword))
                .toList();
    }
}
