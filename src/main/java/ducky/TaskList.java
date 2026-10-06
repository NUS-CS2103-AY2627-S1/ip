package ducky;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Represents the collection of tasks managed by Ducky.
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
     * Creates a task list containing the given tasks.
     *
     * @param tasks the initial tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "A task list must have a source collection";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        assert task != null : "A task list cannot contain a null task";
        tasks.add(task);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index the zero-based task index.
     * @return the task at the index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index the zero-based task index.
     * @return the removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks for storage operations.
     *
     * @return the task list.
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns whether this list already contains a task with the same details.
     *
     * @param candidate the task to compare against existing tasks.
     * @return {@code true} if an equivalent task exists, otherwise {@code false}.
     */
    public boolean containsEquivalent(Task candidate) {
        return tasks.stream().anyMatch(task -> isEquivalent(task, candidate));
    }

    private boolean isEquivalent(Task first, Task second) {
        if (!first.getClass().equals(second.getClass())
                || !first.getDescription().equals(second.getDescription())
                || !first.getTags().equals(second.getTags())) {
            return false;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return firstDeadline.getBy().equals(secondDeadline.getBy());
        }
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            return firstEvent.getStart().equals(secondEvent.getStart())
                    && firstEvent.getEnd().equals(secondEvent.getEnd());
        }
        return true;
    }

    /**
     * Returns tasks whose descriptions contain the given keyword, ignoring letter case.
     *
     * @param keyword the keyword to search for.
     * @return the matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ENGLISH).contains(normalizedKeyword)
                        || task.getTags().stream()
                                .map(tag -> "#" + tag)
                                .anyMatch(tag -> tag.contains(normalizedKeyword)))
                .collect(Collectors.toList());
    }
}
