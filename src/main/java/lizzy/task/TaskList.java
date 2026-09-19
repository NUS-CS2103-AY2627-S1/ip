package lizzy.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import lizzy.exception.LizzyException;

/**
 * Owns Lizzy's ordered collection of tasks and its list-related operations.
 */
public class TaskList {
    /**
     * The tasks in the order they were added.
     */
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list from zero or more supplied tasks.
     *
     * @param tasks the tasks to place in the list, in insertion order
     */
    public TaskList(Task... tasks) {
        this(List.of(tasks));
    }

    /**
     * Creates a task list containing the supplied saved tasks.
     *
     * @param tasks the tasks loaded from storage
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     * @throws LizzyException if an identical task is already in the list
     */
    public void add(Task task) throws LizzyException {
        assert task != null : "A task list cannot contain a null task.";
        boolean hasDuplicate = tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
        if (hasDuplicate) {
            throw new LizzyException("This task is already keeping its place in your list.\n"
                    + "Even a worthy matter need not be introduced twice.");
        }
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based position for presentation purposes.
     *
     * @param index the zero-based position of the task
     * @return the task at that position
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "A list-display index must refer to an existing task.";
        return tasks.get(index);
    }

    /**
     * Returns the most recently added task.
     *
     * @return the last task in the list
     */
    public Task getLast() {
        assert !tasks.isEmpty() : "The last task can be requested only from a non-empty list.";
        return tasks.getLast();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns one-based task numbers whose descriptions contain a keyword, ignoring case.
     *
     * @param keyword the text to search for
     * @return the matching task numbers in task-list order
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

    /**
     * Returns a task referenced by a one-based command number.
     *
     * @param taskNumber the one-based task number from the user
     * @param command the command that requested the task
     * @return the referenced task
     * @throws LizzyException if the task number cannot refer to a task
     */
    public Task getTask(int taskNumber, String command) throws LizzyException {
        validateTaskNumber(taskNumber, command);
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "A validated task number must refer to an existing task.";
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes and returns a task referenced by a one-based command number.
     *
     * @param taskNumber the one-based task number from the user
     * @param command the command that requested the deletion
     * @return the removed task
     * @throws LizzyException if the task number cannot refer to a task
     */
    public Task deleteTask(int taskNumber, String command) throws LizzyException {
        validateTaskNumber(taskNumber, command);
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "A validated task number must refer to an existing task.";
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns an immutable snapshot for saving to storage.
     *
     * @return an immutable copy of the current tasks
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Validates that a one-based task number exists in this list.
     *
     * @param taskNumber the task number supplied by the user
     * @param command the command that requested the task
     * @throws LizzyException if the list is empty or the number is outside its valid range
     */
    private void validateTaskNumber(int taskNumber, String command) throws LizzyException {
        if (tasks.isEmpty()) {
            throw new LizzyException("Ambitious—but there is nothing to " + command
                    + " while your list is empty.\nAdd a task first, and then we shall have something to "
                    + command + ".");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new LizzyException("An imaginative choice—but that task is not on the present list.\n"
                    + "Choose a task number from 1 to " + tasks.size() + ".");
        }
    }
}
