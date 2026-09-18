package meka.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import meka.exception.MekaException;

/**
 * Manages the collection of tasks used by MEKA.
 */
public class TaskList implements Iterable<Task> {
    /**
     * Message shown when a command refers to a task outside the list.
     */
    private static final String INVALID_TASK_NUMBER_MESSAGE =
            "The task number does not exist in the list.";

    /** Tasks in the order in which they appear to the user. */
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in their current order.
     *
     * @param tasks tasks with which to initialize the list.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "A task list should be created from a collection";

        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "A task list should contain only task objects";

        tasks.add(task);
    }

    /**
     * Returns the task identified by a one-based task number.
     *
     * @param taskNumber one-based task number supplied by the user.
     * @return the selected task.
     * @throws MekaException if the number is outside the task list.
     */
    public Task get(int taskNumber) throws MekaException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MekaException(INVALID_TASK_NUMBER_MESSAGE);
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes and returns the task identified by a one-based task number.
     *
     * @param taskNumber one-based task number supplied by the user.
     * @return the removed task.
     * @throws MekaException if the number is outside the task list.
     */
    public Task delete(int taskNumber) throws MekaException {
        Task task = get(taskNumber);
        tasks.remove(taskNumber - 1);
        return task;
    }

    /**
     * Removes every task from the list.
     */
    public void clear() {
        tasks.clear();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns matching tasks in their original order without modifying this list.
     *
     * @param keyword Description text to match, ignoring letter case.
     * @return A separate list containing the matching tasks.
     */
    public TaskList find(String keyword) {
        ArrayList<Task> matches = tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(matches);
    }

    /**
     * Returns an iterator over the tasks in display order.
     *
     * @return task iterator.
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
