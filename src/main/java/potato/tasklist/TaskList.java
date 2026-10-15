package potato.tasklist;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import potato.exception.PotatoException;
import potato.task.Task;

/**
 * Manages the in-memory list of tasks and supports operations to modify them.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs a {@code TaskList} pre-populated with an existing list of tasks.
     *
     * @param tasks Initial list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Initial task list collection must not be null";
        this.tasks = tasks;
    }

    /**
     * Constructs an empty {@code TaskList}.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Filters tasks matching a keyword using Java Streams.
     *
     * @param keyword Search term to match against task descriptions.
     * @return New {@code TaskList} containing matching tasks.
     */
    public TaskList findMatchingTasks(String keyword) {
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        return new TaskList(new ArrayList<>(matchingTasks));
    }

    /**
     * Returns the internal list of tasks.
     *
     * @return {@code ArrayList} containing all tasks.
     */
    public ArrayList<Task> getTasks() {
        assert tasks != null : "Internal tasks list should never be null";
        return tasks;
    }

    /**
     * Appends a task to the task list.
     *
     * @param task Task to be added.
     */
    public void add(Task task) {
        assert task != null : "Cannot add a null task to TaskList";
        int previousSize = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousSize + 1 : "Task list size should increment by 1 after adding a task";
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index Zero-based index of the task to remove.
     * @return The removed task.
     * @throws PotatoException If the index is out of bounds.
     */
    public Task remove(int index) throws PotatoException {
        validateIndex(index);
        int previousSize = tasks.size();
        Task removedTask = tasks.remove(index);
        assert removedTask != null : "Removed task should not be null";
        assert tasks.size() == previousSize - 1 : "Task list size should decrement by 1 after removal";
        return removedTask;
    }

    /**
     * Retrieves the task at the specified index.
     *
     * @param index Zero-based index of the task to retrieve.
     * @return Task at the specified index.
     * @throws PotatoException If the index is out of bounds.
     */
    public Task get(int index) throws PotatoException {
        validateIndex(index);
        Task retrievedTask = tasks.get(index);
        assert retrievedTask != null : "Retrieved task at valid index should not be null";
        return retrievedTask;
    }

    /**
     * Returns the total number of tasks in the list.
     *
     * @return Total task count.
     */
    public int size() {
        assert tasks != null : "Internal tasks list should never be null when checking size";
        return tasks.size();
    }

    /**
     * Sorts tasks using the provided comparator.
     *
     * @param comparator Comparator for ordering tasks.
     */
    public void sortTasks(Comparator<Task> comparator) {
        tasks.sort(comparator);
    }

    /**
     * Validates if the specified index is within valid task list bounds.
     *
     * @param index Zero-based index to check.
     * @throws PotatoException If the index is less than 0 or greater than/equal to size.
     */
    private void validateIndex(int index) throws PotatoException {
        if (index < 0 || index >= tasks.size()) {
            throw new PotatoException("OOPS!!! Task number " + (index + 1) + " does not exist.");
        }
    }
}