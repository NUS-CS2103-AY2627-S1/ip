package student.project.bob.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import student.project.bob.exception.BobException;

/**
 * Stores and manages Bob's tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to copy into this list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based index of the task
     * @return task at the specified index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at the specified zero-based index.
     *
     * @param index zero-based insertion index
     * @param task task to insert
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based index of the task to remove
     * @return removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Converts a command's task number into a zero-based list index.
     *
     * @param parts command words, such as ["mark", "1"]
     * @param commandName command being validated
     * @return the zero-based task index
     * @throws BobException if the command does not contain a valid task number
     */
    public int getIndex(String[] parts, String commandName) throws BobException {
        if (parts.length != 2) {
            throw new BobException("Use \"" + commandName + " <task number>\". Example: \"" + commandName + " 1\".");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);

            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new BobException(
                        "That task number does not exist. Use \"list\" to view the available task numbers.");
            }

            int index = taskNumber - 1;
            assert index >= 0 && index < tasks.size() : "A valid task number must map to a valid list index";
            return index;
        } catch (NumberFormatException e) {
            throw new BobException("The task number must be a whole number. Example: \"" + commandName + " 1\".");
        }
    }

    /**
     * Returns a read-only view of the tasks.
     *
     * @return unmodifiable task list view
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns a stream over the tasks.
     *
     * @return stream of tasks
     */
    public Stream<Task> stream() {
        return tasks.stream();
    }
}
