package rice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stores and manages the current list of tasks.
 */
public class TaskList {
    private List<Task> taskList;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.taskList = new ArrayList<>();
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        taskList.add(task);
    }

    /**
     * Removes and returns the task at the zero-based index.
     *
     * @param index zero-based task index
     * @return removed task
     */
    public Task remove(int index) {
        assert index >= 0 && index < taskList.size();
        return taskList.remove(index);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index index of the task to retrieve
     * @return task at the given index
     */
    public Task get(int index) {
        assert index >= 0 && index < taskList.size();
        return taskList.get(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return current number of tasks
     */
    public int size() {
        return taskList.size();
    }

    /**
     * Returns the internal list of tasks.
     *
     * @return all tasks currently stored
     */
    public List<Task> getTasks() {
        return taskList;
    }

    /**
     * Adds all supplied tasks to this list.
     *
     * @param tasks tasks to add
     */
    public void addAll(List<Task> tasks) {
        taskList.addAll(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword search keyword
     * @return matching tasks
     */
    public List<Task> find(String keyword) {
        String searchTerm = keyword.toLowerCase();

        return taskList.stream()
                       .filter(task -> task.getTask().toLowerCase().contains(searchTerm))
                       .toList();
    }

    /**
     * Marks the task at the zero-based index as complete.
     *
     * @param index zero-based task index
     */
    public void mark(int index) {
        get(index).mark();
    }

    /**
     * Marks the task at the zero-based index as incomplete.
     *
     * @param index zero-based task index
     */
    public void unmark(int index) {
        get(index).unmark();
    }

    /**
     * Sorts tasks by their deadline, placing tasks without a deadline last.
     */
    public void sortTasks() {
        taskList.sort(Comparator.comparing(Task::getDeadlineDate));
    }
}
