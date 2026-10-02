package clsl.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import clsl.ClslException;

/**
 * Stores and manages the tasks in the application.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks Tasks to include in this task list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Loaded task list must not be null";
        this.tasks = new ArrayList<>();
        for (Task task : tasks) {
            if (!containsDuplicate(task)) {
                this.tasks.add(task);
            }
        }
    }

    /**
     * Adds a task to the end of the list if it is not already present.
     *
     * @param task Task to add.
     * @throws ClslException If an equivalent task is already in the list.
     */
    public void add(Task task) throws ClslException {
        if (containsDuplicate(task)) {
            throw new ClslException("already added");
        }
        tasks.add(task);
    }

    /** Returns whether this list already contains a task equivalent to the specified task. */
    private boolean containsDuplicate(Task task) {
        for (Task existingTask : tasks) {
            if (existingTask.isDuplicateOf(task)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task to remove.
     * @return The removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /** Marks the task at the specified zero-based index as complete. */
    public void mark(int index) {
        tasks.get(index).markAsDone();
    }

    /** Marks the task at the specified zero-based index as incomplete. */
    public void unmark(int index) {
        tasks.get(index).unmarkAsDone();
    }

    /** Returns the task at the specified zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks that occur on the given date.
     *
     * @param date Date to check.
     * @return Tasks that occur on the given date.
     */
    public List<Task> getTasksOn(LocalDate date) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns the tasks whose descriptions contain the supplied keyword.
     * Matching is case-insensitive.
     *
     * @param keyword keyword to search for
     * @return tasks whose descriptions contain the keyword
     */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase();
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getName().toLowerCase().contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns an immutable view of the tasks for displaying or saving them.
     *
     * @return The tasks in this task list.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
