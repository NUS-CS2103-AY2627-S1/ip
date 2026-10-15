package doge.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Maintains the ordered collection of tasks used by the application. */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    /** Creates an empty task list. */
    public TaskList() {
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("A task list must not contain null tasks");
        }
        tasks.add(task);
    }

    /** Returns the task at a one-based index. */
    public Task get(int index) {
        validateIndex(index);
        return tasks.get(index - 1);
    }

    /** Removes and returns the task at a one-based index. */
    public Task delete(int index) {
        validateIndex(index);
        return tasks.remove(index - 1);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns the underlying ordered task collection. */
    public List<Task> getTasks() {
        return tasks;
    }

    /** Marks the task at a one-based index as completed. */
    public void markDone(int index) {
        validateIndex(index);
        tasks.get(index - 1).markDone();
    }

    /** Marks the task at a one-based index as incomplete. */
    public void unmarkDone(int index) {
        validateIndex(index);
        tasks.get(index - 1).unmarkDone();
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     * Matching is case-insensitive and follows the order in which tasks were added.
     *
     * @param keyword text to search for.
     * @return matching tasks, or an empty list if there are no matches.
     */
    public List<Task> find(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("Search keyword must not be null");
        }
        String searchTerm = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(searchTerm)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /** Returns a numbered, display-ready representation of all tasks. */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            result.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return result.toString();
    }

    /** Returns whether an index follows this class's one-based indexing contract. */
    private boolean isValidIndex(int index) {
        return index >= 1 && index <= tasks.size();
    }

    private void validateIndex(int index) {
        if (!isValidIndex(index)) {
            throw new IndexOutOfBoundsException("Task index must be one-based and within the list");
        }
    }
}
