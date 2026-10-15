package cheecken;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the chatbot's tasks and exposes task-list operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the list.
     * @param task task to process
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     * @param index zero-based task index
     * @return task at the specified index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     * @param index zero-based task index
     * @return removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks.
     * @return number of tasks in the list
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an immutable snapshot of the tasks.
     * @return immutable snapshot of the task list
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, in list order.
     * @param keyword keyword to match against task descriptions
     * @return matching tasks in insertion order
     */
    public List<Task> find(String keyword) {
        return tasks.stream().filter(task -> task.containsKeyword(keyword)).toList();
    }
}
