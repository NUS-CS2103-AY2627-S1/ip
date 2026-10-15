package bot67.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Owns Bot67's tasks and provides operations for changing the task list.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        assert tasks.stream().noneMatch(task -> task == null) : "Initial task list must not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        int originalSize = tasks.size();
        tasks.add(task);
        assert tasks.size() == originalSize + 1 : "Adding a task must increase the list size by one";
    }

    /** Removes and returns the task at the one-based position. */
    public Task delete(int taskNumber) {
        assert isValidTaskNumber(taskNumber) : "Task number to delete must be within the list";
        int originalSize = tasks.size();
        Task deletedTask = tasks.remove(taskNumber - 1);
        assert deletedTask != null : "Deleted task must not be null";
        assert tasks.size() == originalSize - 1 : "Deleting a task must decrease the list size by one";
        return deletedTask;
    }

    /** Marks the task at the one-based position as done. */
    public void mark(int taskNumber) {
        get(taskNumber).mark();
    }

    /** Marks the task at the one-based position as not done. */
    public void unmark(int taskNumber) {
        get(taskNumber).unmark();
    }

    /** Sorts tasks alphabetically by name without changing their contents. */
    public void sortByName() {
        tasks.sort(Comparator.comparing(Task::getName, String.CASE_INSENSITIVE_ORDER));
    }

    /** Returns the task at the one-based position. */
    public Task get(int taskNumber) {
        return tasks.get(taskNumber - 1);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns the tasks for persistence. */
    public List<Task> asList() {
        return tasks;
    }

    /** Returns whether the one-based task number identifies an existing task. */
    private boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }
}
