package nexus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Manages the tasks in the current Nexus session. */
public class TaskList {
    /** Tasks maintained in their display order. */
    private final List<Task> tasks;

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task collection must not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the list. */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /** Returns a task using its one-based display index. */
    public Task get(int index) {
        return tasks.get(index - 1);
    }

    /** Removes and returns a task using its one-based display index. */
    public Task delete(int index) {
        return tasks.remove(index - 1);
    }

    /** Inserts a task at a one-based position. */
    public void insert(int index, Task task) {
        tasks.add(index - 1, task);
    }

    /** Replaces the current task order with the supplied snapshot. */
    public void replaceAll(List<Task> replacement) {
        tasks.clear();
        tasks.addAll(replacement);
    }

    /** Sorts tasks alphabetically by description, ignoring letter case. */
    public void sortByDescription() {
        tasks.sort(Comparator.comparing(Task::getDescription,
                String.CASE_INSENSITIVE_ORDER));
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns all tasks for display or storage. */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }
}
