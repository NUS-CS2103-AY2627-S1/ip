package dingleberry.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps the in-memory list of tasks and the operations Dingleberry performs
 * on it (adding, deleting, looking up, and counting tasks), so callers don't
 * manipulate a raw {@link ArrayList} directly.
 */
public class TaskList {
    /** Stores the ordered collection of tasks. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in their given order.
     *
     * @param initialTasks the tasks to add to the new list.
     */
    public TaskList(final Task... initialTasks) {
        this.tasks = new ArrayList<>(List.of(initialTasks));
    }

    /**
     * Wraps an already-loaded list of tasks, e.g. one read from storage.
     *
     * @param loadedTasks the tasks to manage.
     */
    public TaskList(final List<Task> loadedTasks) {
        this.tasks = new ArrayList<>(loadedTasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to append.
     */
    public void add(final Task task) {
        assert task != null : "A task list must not contain null tasks.";
        tasks.add(task);
    }

    /**
     * Returns whether this list already contains the same task content.
     *
     * @param candidate the task to compare with existing tasks.
     * @return true if a task has the same type, description, and dates.
     */
    public boolean containsEquivalentTask(final Task candidate) {
        return tasks.stream()
            .anyMatch(task -> task.hasSameContentAs(candidate));
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index the zero-based position to remove.
     * @return the removed task.
     */
    public Task remove(final int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index the zero-based position to access.
     * @return the task at the specified position.
     */
    public final Task get(final int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the task count.
     */
    public final int size() {
        return tasks.size();
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword the text to search for.
     * @return a list of matching tasks.
     */
    public TaskList findByKeyword(final String keyword) {
        final String keywordLowerCase = keyword.toLowerCase();
        final List<Task> filteredTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase()
                    .contains(keywordLowerCase))
                .toList();
        return new TaskList(filteredTasks);
    }

    /**
     * Returns a copy of the tasks, e.g. for {@link Storage} to persist.
     *
     * @return a copy of the managed task collection.
     */
    public ArrayList<Task> asArrayList() {
        return new ArrayList<>(tasks);
    }
}
