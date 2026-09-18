package alice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents a list of tasks and provides operations to add, remove,
 * retrieve, and query tasks within the list.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList backed by the given list of tasks, such as one
     * loaded from storage.
     *
     * @param tasks the initial list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "the list loaded from storage must not be null";
        this.tasks = tasks;
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
     * Removes and returns the task at the given index.
     *
     * @param index the zero-based index of the task to remove.
     * @return the removed task.
     */
    public Task remove(int index) {
        assert isValidIndex(index) : "index must be within bounds before removal";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index the zero-based index of the task.
     * @return the task at that index.
     */
    public Task get(int index) {
        assert isValidIndex(index) : "index must be within bounds before retrieval";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks whose description contains at least one of the
     * given keywords (a case-sensitive substring match). Passing a single
     * keyword behaves exactly as a single-keyword search always did;
     * passing several returns their union (any task matching at least one
     * keyword), with each matching task included only once even if it
     * matches more than one keyword, and in their original list order.
     *
     * @param keywords one or more keywords to search for.
     * @return a list of matching tasks.
     */
    public ArrayList<Task> find(String... keywords) {
        assert keywords != null && keywords.length > 0 : "at least one keyword must be given";
        return tasks.stream()
                .filter(task -> Arrays.stream(keywords).anyMatch(keyword -> task.getDescription().contains(keyword)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Checks whether the given index refers to a valid position within
     * the list.
     *
     * @param index the zero-based index to check.
     * @return true if the index is within bounds, false otherwise.
     */
    public boolean isValidIndex(int index) {

        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns the underlying list of all tasks.
     *
     * @return the full list of tasks.
     */
    public ArrayList<Task> getAllTasks() {
        return tasks;
    }
}
