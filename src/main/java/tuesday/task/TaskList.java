package tuesday.task;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Stores and manages the tasks used by Tuesday.
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
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at a zero-based index, preserving the order of surrounding tasks.
     *
     * @param index the insertion position.
     * @param task the task to insert.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Finds the first task with the same normalized description as the supplied task.
     *
     * @param task the proposed task.
     * @return the zero-based index of the first duplicate, or -1 if none exists.
     */
    public int indexOfDuplicate(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).hasSameDescription(task)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Replaces and returns the task at a zero-based index.
     *
     * @param index the task's zero-based index.
     * @param task the replacement task.
     * @return the replaced task.
     */
    public Task replace(int index, Task task) {
        return tasks.set(index, task);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the task's zero-based index.
     * @return the removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at a zero-based index without removing it.
     *
     * @param index the task's zero-based index.
     * @return the task at the requested index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a sequential stream containing the stored tasks.
     *
     * @return a stream of tasks.
     */
    public Stream<Task> stream() {
        return tasks.stream();
    }
}
