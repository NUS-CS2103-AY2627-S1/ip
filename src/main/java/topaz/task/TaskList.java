package topaz.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Stores the tasks currently managed by Topaz.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks the tasks to copy into this list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param taskIndex the zero-based task index
     * @return the task at the requested index
     */
    public Task get(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /**
     * Returns whether the list already contains a task with the same details.
     */
    public boolean containsDuplicate(Task candidate) {
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at the given zero-based index.
     *
     * @param taskIndex the zero-based insertion index
     * @param task the task to insert
     */
    public void add(int taskIndex, Task task) {
        tasks.add(taskIndex, task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param taskIndex the zero-based task index
     * @return the removed task
     */
    public Task remove(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /**
     * Marks the task at the given zero-based index as done.
     *
     * @param taskIndex the zero-based task index
     */
    public void markAsDone(int taskIndex) {
        tasks.get(taskIndex).markAsDone();
    }

    /**
     * Marks the task at the given zero-based index as not done.
     *
     * @param taskIndex the zero-based task index
     */
    public void markAsNotDone(int taskIndex) {
        tasks.get(taskIndex).markAsNotDone();
    }

    /**
     * Returns a read-only view of the tasks for display or storage.
     *
     * @return an unmodifiable view of the tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword the text to search for
     * @return matching tasks in their original list order
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ENGLISH).contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
}
