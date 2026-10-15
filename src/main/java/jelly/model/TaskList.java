package jelly.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/** Owns Jelly's collection of tasks and its basic list operations. */
public class TaskList {
    /** The tasks managed by this list. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks the initial tasks to copy into this list.
     */
    public TaskList(Collection<? extends Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task the task to add.
     */
    public void addTask(Task task) {
        assert task != null : "Task cannot be null";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the zero-based position of the task to remove.
     * @return the removed task.
     */
    public Task deleteTask(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index the zero-based position of the task to retrieve.
     * @return the task at the requested position.
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks.
     *
     * @return the number of tasks currently in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Provides read-only access for rendering and persistence.
     *
     * @return an unmodifiable snapshot of the tasks.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Finds one-based positions of tasks whose descriptions contain a keyword.
     *
     * @param keyword the keyword to search for
     * @return matching task positions in their original order
     */
    public List<Integer> findMatchingTaskNumbers(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .map(index -> index + 1)
                .boxed()
                .toList();
    }
}
