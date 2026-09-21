package thaisbot.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Container for a mutable list of tasks. Provides convenience methods used by commands.
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /**
     * Create an empty TaskList.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Create a TaskList from an existing list of tasks.
     * @param tasks initial tasks
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null;
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Add a task to the list.
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null;
        tasks.add(task);
    }

    /**
     * Remove and return the task at the given index.
     * @param index zero-based index
     * @return removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Get the task at the given index.
     * @param index zero-based index
     * @return task at index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Current number of tasks in the list.
     * @return size
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a sequential stream over the tasks.
     * @return stream of tasks
     */
    public Stream<Task> stream() {
        return tasks.stream();
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
