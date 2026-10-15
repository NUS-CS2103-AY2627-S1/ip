package jiji.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates the collection of tasks and provides operations to add, delete,
 * retrieve, and inspect tasks in the list.
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList populated with the specified initial list of tasks.
     *
     * @param tasks Initial list of tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = (tasks != null) ? tasks : new ArrayList<>();
        assert this.tasks != null : "Internal task collection must not be null";
    }

    /**
     * Adds a new task to the list.
     *
     * @param task The task to be added.
     */
    public void add(Task task) {
        assert task != null : "Task to add cannot be null";
        tasks.add(task);
        assert tasks.contains(task) : "TaskList should contain added task";
    }

    /**
     * Adds multiple tasks to the list.
     *
     * @param tasks Variable number of {@link Task} objects to add.
     */
    public void addAll(Task... tasks) {
        if (tasks != null) {
            for (Task task : tasks) {
                assert task != null : "Cannot add null task to TaskList";
                this.tasks.add(task);
            }
        }
    }

    /**
     * Removes and returns the task at the specified 0-based index.
     *
     * @param index The 0-based index of the task to remove.
     * @return The removed {@link Task}.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public Task delete(int index) {
        Task removed = tasks.remove(index);
        assert removed != null : "Removed task should not be null";
        return removed;
    }

    /**
     * Returns the task at the specified 0-based index.
     *
     * @param index The 0-based index of the task.
     * @return The {@link Task} at the given index.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public Task get(int index) {
        Task task = tasks.get(index);
        assert task != null : "Retrieved task should not be null";
        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Total count of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks if the task list is empty.
     *
     * @return True if there are no tasks, false otherwise.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Checks if a duplicate of the specified task already exists in the list.
     *
     * @param task The task to check.
     * @return The existing duplicate task, or null if no duplicate is found.
     */
    public Task findDuplicate(Task task) {
        if (task == null) {
            return null;
        }
        return tasks.stream()
                .filter(existing -> existing.isDuplicateOf(task))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds and returns all tasks whose descriptions contain the specified keyword.
     * The search is case-insensitive.
     *
     * @param keyword The search keyword.
     * @return A list of matching tasks.
     */
    public List<Task> findTasks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return The list of tasks.
     */
    public List<Task> getAllTasks() {
        assert tasks != null : "Underlying task list cannot be null";
        return tasks;
    }

    /**
     * Counts the number of completed tasks in the list.
     *
     * @return Number of tasks marked as completed.
     */
    public int countCompleted() {
        return (int) tasks.stream()
                .filter(Task::isDone)
                .count();
    }

    /**
     * Counts the number of incomplete (pending) tasks in the list.
     *
     * @return Number of pending tasks.
     */
    public int countPending() {
        return size() - countCompleted();
    }

    /**
     * Counts the number of tasks matching the specified task subclass.
     *
     * @param type The task class type to filter for.
     * @return Number of tasks of the specified type.
     */
    public int countByType(Class<? extends Task> type) {
        assert type != null : "Task type class cannot be null";
        return (int) tasks.stream()
                .filter(type::isInstance)
                .count();
    }

    /**
     * Counts the number of completed tasks matching the specified task subclass.
     *
     * @param type The task class type to filter for.
     * @return Number of completed tasks of the specified type.
     */
    public int countCompletedByType(Class<? extends Task> type) {
        assert type != null : "Task type class cannot be null";
        return (int) tasks.stream()
                .filter(task -> type.isInstance(task) && task.isDone())
                .count();
    }

    /**
     * Counts the number of incomplete deadlines that are overdue relative to the given reference date.
     *
     * @param today The reference date.
     * @return Number of overdue, incomplete deadlines.
     */
    public int countOverdueDeadlines(LocalDate today) {
        return (int) tasks.stream()
                .filter(task -> task instanceof Deadline)
                .map(task -> (Deadline) task)
                .filter(deadline -> deadline.isOverdue(today))
                .count();
    }
}
