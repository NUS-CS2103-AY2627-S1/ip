package lion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Manages the ordered collection of tasks used by the application.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by an existing list of tasks.
     *
     * @param tasks existing tasks, in order.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "backing task list must not be null";

        this.tasks = tasks;
    }

    /**
     * Returns the number of active tasks.
     *
     * @return number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index zero-based task index.
     * @return task at the index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "task to add must not be null";

        tasks.add(task);
    }

    /**
     * Removes and returns a task while keeping the remaining tasks contiguous.
     *
     * @param index zero-based index of the task to remove.
     * @return removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks the task at the specified index as complete.
     *
     * @param index zero-based task index.
     */
    public void mark(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at the specified index as incomplete.
     *
     * @param index zero-based task index.
     */
    public void unmark(int index) {
        tasks.get(index).markAsNotDone();
    }

    /**
     * Returns whether a task with the same details as the given task is already present.
     *
     * @param candidate task to check for duplicates of; not itself required to be in this list.
     * @return true if an existing task has the same type, description, and (for
     *     deadlines/events) date fields as the candidate.
     */
    public boolean hasDuplicate(Task candidate) {
        return stream().anyMatch(existing -> existing.hasSameDetails(candidate));
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     * Matching is case-sensitive and preserves the original task order.
     *
     * @param keyword keyword to find in task descriptions.
     * @return task list containing the matching tasks.
     */
    public TaskList find(String keyword) {
        assert keyword != null : "search keyword must not be null";

        TaskList matches = new TaskList();
        stream()
                .filter(task -> task.getDescription().contains(keyword))
                .forEach(matches::add);
        return matches;
    }

    /**
     * Returns a stream over the active tasks, in their current order.
     *
     * @return stream of the active tasks.
     */
    public Stream<Task> stream() {
        return tasks.stream();
    }

    /**
     * Saves the active tasks to disk.
     *
     * @throws IOException if the tasks cannot be written.
     */
    public void save() throws IOException {
        Storage.save(tasks);
    }
}
