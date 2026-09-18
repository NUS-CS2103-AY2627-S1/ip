package friday.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Encapsulates the collection of tasks managed by Friday.
 *
 * The list is kept private so command-handling code cannot mutate it without
 * going through this class's task-list API.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in argument order.
     *
     * @param tasks Tasks to place in this list.
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks Tasks to place in this list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Checks whether this list already contains equivalent task details.
     *
     * @param candidate Task whose details should be checked.
     * @return Whether an equivalent task is already present.
     */
    public boolean containsEquivalent(Task candidate) {
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    /**
     * Gets the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return The task at the requested index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return The removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Replaces this list's contents with a copy of the supplied tasks.
     *
     * @param tasks Tasks that should replace the current list contents.
     */
    public void replaceWith(List<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return The task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Provides a read-only view for components such as storage and display.
     *
     * @return An unmodifiable view of the tasks.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the supplied keyword,
     * ignoring letter case.
     *
     * @param keyword Text to search for in each task description.
     * @return Matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }
}
