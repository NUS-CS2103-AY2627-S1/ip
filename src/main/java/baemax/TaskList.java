package baemax;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The list of tasks the user is tracking, together with the operations the
 * chatbot performs on it. Wrapping the {@link ArrayList} lets callers work in
 * terms of tasks rather than list mechanics.
 */
public class TaskList {
    /** Tasks in the order the user entered them. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list seeded with existing tasks, such as those loaded
     * from the save file.
     *
     * @param initialTasks the tasks to start with
     */
    public TaskList(List<Task> initialTasks) {
        // Storage.load() always returns a list, even when empty, so a null
        // here would mean a caller broke that contract.
        assert initialTasks != null : "initialTasks must not be null";
        this.tasks = new ArrayList<>(initialTasks);
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
     * Removes the task at the given one-based position.
     *
     * @param oneBasedIndex the task number shown to the user
     * @return the removed task
     */
    public Task remove(int oneBasedIndex) {
        return tasks.remove(oneBasedIndex - 1);
    }

    /**
     * Returns the task at the given one-based position.
     *
     * @param oneBasedIndex the task number shown to the user
     * @return the task at that position
     */
    public Task get(int oneBasedIndex) {
        return tasks.get(oneBasedIndex - 1);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks as a list for reading, such as when saving or
     * displaying them. The returned list is the backing list and is not meant
     * to be modified directly.
     *
     * @return the tasks in list order
     */
    public List<Task> asList() {
        return tasks;
    }

    /**
     * Returns the tasks whose description contains the given keyword, matched
     * without regard to case, in list order.
     *
     * @param keyword the text to search for
     * @return the matching tasks, or an empty list when none match
     */
    public List<Task> find(String keyword) {
        String needle = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }

    /**
     * Returns an independent copy of every task in this list, for restoring
     * later via {@link #restoreFrom}. Each task is copied rather than
     * referenced, so a later change such as {@code markAsDone} on a live
     * task does not alter the snapshot.
     *
     * @return a snapshot of the current tasks, in list order
     */
    public List<Task> snapshot() {
        return tasks.stream()
                .map(Task::copy)
                .collect(Collectors.toList());
    }

    /**
     * Replaces every task in this list with the given ones, e.g. to undo back
     * to a snapshot taken before a command ran.
     *
     * @param snapshot the tasks to restore, in list order
     */
    public void restoreFrom(List<Task> snapshot) {
        tasks.clear();
        tasks.addAll(snapshot);
    }
}
