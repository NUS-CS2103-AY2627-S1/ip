package alexa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * Owns Alexa's ordered collection of tasks and provides operations on that collection.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Returns the tasks whose descriptions contain the given keyword, in list order.
     *
     * @param keyword The keyword to search for.
     * @return The matching tasks.
     */
    public List<Task> findTasks(String keyword) {
        return tasks.stream().filter(task -> task.hasDescriptionContaining(keyword)).toList();
    }

    /**
     * Returns the one-based number of the first task identical to the given task.
     *
     * @param task The task to compare with the tasks in this list.
     * @return The duplicate task number, or an empty result when no duplicate exists.
     */
    public OptionalInt findDuplicateTaskNumber(Task task) {
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).hasSameIdentity(task))
                .map(index -> index + 1)
                .findFirst();
    }

    /**
     * Creates a task list containing the supplied tasks in their existing order.
     *
     * @param tasks The tasks with which to initialize this list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based position.
     *
     * @param index The zero-based task position.
     * @return The requested task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based position.
     *
     * @param index The zero-based task position.
     * @return The removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
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
     * Returns a read-only view of the tasks for persistence.
     *
     * @return The tasks in their current order.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
