package kotha.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Owns the tasks currently managed by Kotha. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "A task list must be constructed from a non-null collection";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Creates an empty task list*/
    public TaskList() {
        this.tasks = new ArrayList<>();
    }
    /** Adds a task to the list. */
    public void add(Task task) {
        assert task != null : "The task list must not contain null tasks";
        tasks.add(task);
    }

    /** Returns the task at the zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes and returns the task at the zero-based index. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns a read-only copy for display and persistence. */
    public List<Task> asList() {
        assert tasks.stream().noneMatch(task -> task == null)
                : "The task list invariant forbids null tasks";
        return List.copyOf(tasks);
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring case. */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
    }
}
