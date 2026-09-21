package chris;

import java.util.ArrayList;
import java.util.List;

/** Owns the task collection and provides task-related operations. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates a task list containing the supplied tasks. */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Task collection must be initialized";
        this.tasks = tasks;
    }

    /** Adds a task. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /** Restores a task to its former position if saving a deletion fails. */
    public void insert(int index, Task task) {
        tasks.add(index, task);
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns a read-only view for display and storage. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring case. */
    public List<Task> find(String keyword) {
        return tasks.stream().filter(task -> task.matches(keyword)).toList();
    }
}
