package oreo.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Owns the in-memory collection of tasks and provides the operations used by
 * the command loop.
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "A task list must be initialized from a task collection.";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the list. */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("The task list must not contain null tasks.");
        }
        if (tasks.stream().anyMatch(existing -> existing.toString().equals(task.toString()))) {
            throw new IllegalArgumentException("A task with the same details already exists.");
        }
        tasks.add(task);
    }

    /** Removes a task from the list. */
    public void remove(Task task) {
        tasks.remove(task);
    }

    /** Returns the task at the zero-based index. */
    public Task get(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("The requested task index is not valid.");
        }
        return tasks.get(index);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether the list has no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Provides a read-only traversal view for persistence and display. */
    public Stream<Task> stream() {
        return tasks.stream();
    }

    /**
     * Finds tasks whose descriptions contain the supplied keyword.
     * Matching ignores letter case and preserves the original task order.
     *
     * @param keyword the text to search for
     * @return a new list containing matching tasks
     */
    public TaskList find(String keyword) {
        String searchText = keyword.toLowerCase(Locale.ROOT);
        return new TaskList(tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(searchText))
                .toList());
    }

    /** Finds tasks whose merchandise details contain the supplied keyword. */
    public TaskList findMerchandise(String keyword) {
        String searchText = keyword.toLowerCase(Locale.ROOT);
        return new TaskList(tasks.stream()
                .filter(task -> task.hasMerchandise()
                        && task.getMerchandise().toLowerCase(Locale.ROOT).contains(searchText))
                .toList());
    }

    /** Returns an iterator over the tasks in list order. */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
