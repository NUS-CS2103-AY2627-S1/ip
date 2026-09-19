package nyonbot.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Stores <code>Tasks</code> in insertion order.
 * <p>
 * Supports iteration and some basic <code>ArrayList</code> functions.
 * @author nje14
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /**
     * Creates an empty TaskList.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Inserts a new <code>Task</code> to the end of the list.
     *
     * @param task Task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Adds all associated tasks to the TaskList.
     *
     * @param taskCollection tasks to insert.
     * @return true if successsful.
     */
    public boolean addAll(Collection<? extends Task> taskCollection) {
        return tasks.addAll(taskCollection);
    }

    /**
     * Removes the <code>Task</code> at the specified index.
     *
     * @param index zero-based index of the <code>Task</code>.
     * @return the <code>Task</code> removed.
     * @throws IndexOutOfBoundsException if index is outside of range.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Removes the <code>Task</code> specified from the list.
     *
     * @param task the <code>Task</code> to be removed.
     * @return true iff the task was removed.
     */
    public boolean remove(Task task) {
        return tasks.remove(task);
    }

    /**
     * Returns the <code>Task</code> at the specified index.
     *
     * @param index zero-based index to look up.
     * @return the <code>Task</code> associated with that index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Clears the current <code>TaskList</code>.
     */
    public void clear() {
        tasks.clear();
    }

    /**
     * Returns the size of the TaskList.
     *
     * @return the current size of the TaskList.
     */
    public int size() {
        return tasks.size();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    /**
     * Returns a Stream of this TaskList.
     *
     * @return a Stream of the arraylist.
     */
    public Stream<Task> stream() {
        return tasks.stream();
    }

    /**
     * Returns true if the TaskList is empty.
     *
     * @return true if the TaskList has no element.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }
}
