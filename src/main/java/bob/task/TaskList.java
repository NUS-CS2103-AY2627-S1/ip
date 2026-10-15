package bob.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Represents a list of tasks and provides operations to manipulate the tasks.
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList containing the tasks from the specified list.
     *
     * @param tasks the initial list of tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks != null ? new ArrayList<>(tasks) : new ArrayList<>();
        assert this.tasks != null : "Backing task list should not be null";
    }

    /**
     * Finds tasks whose description contains the keyword, returning each matching
     * task
     * along with its 1-based position in the task list.
     *
     * @param keyword the search term to filter tasks by
     * @return a list of entries mapping 1-based task index to the matching task
     */
    public List<Map.Entry<Integer, Task>> find(String keyword) {
        return IntStream.range(0, this.tasks.size())
                .filter(i -> this.tasks.get(i).containsKeyword(keyword)).<Map.Entry<Integer, Task>>mapToObj(
                        i -> Map.entry(i + 1, this.tasks.get(i)))
                .toList();
    }

    /**
     * Adds a task to the list.
     *
     * @param task the task to be added
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";
        int initialSize = this.tasks.size();
        this.tasks.add(task);
        assert this.tasks.size() == initialSize + 1 : "Task list size should increment by 1 after add";
    }

    /**
     * Retrieves the task at the specified index.
     *
     * @param index the index of the task to return
     * @return the task at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Replaces the task at the specified index with the given task.
     *
     * @param index the index of the task to replace
     * @param task  the replacement task
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void set(int index, Task task) {
        assert task != null : "Replacement task should not be null";
        this.tasks.set(index, task);
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index the index of the task to be removed
     * @return the removed task
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Task remove(int index) {
        return this.tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Checks if the task list is empty.
     *
     * @return true if the list contains no tasks, false otherwise
     */
    public boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    /**
     * Returns an iterator over the tasks in this list.
     *
     * @return an iterator over the tasks
     */
    @Override
    public Iterator<Task> iterator() {
        return this.tasks.iterator();
    }
}
