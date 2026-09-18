package hamtaro.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Stores and provides access to the tasks currently managed by the application. */
public class TaskList {
    private ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<Task>();
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(ArrayList<Task> tasks) {
        // Storage supplies a complete list; a null list would make every operation invalid.
        assert tasks != null : "Task list storage must not be null";
        this.tasks = tasks;
    }

    /** Adds a task to the end of this list. */
    public void addTask(Task task){
        // Commands create a task before handing it to the list.
        assert task != null : "A task list cannot contain null tasks";
        tasks.add(task);
    }

    /** Removes and returns the task at the specified zero-based index. */
    public Task deleteTask(int index) {
        // Callers use a zero-based index that must refer to an existing task.
        assert index >= 0 && index < tasks.size() : "Task index must be valid";
        return tasks.remove(index);
    }

    /** Returns the number of tasks in this list. */
    public int getSize() {
        return this.tasks.size();
    }

    /** Returns the task at the specified zero-based index. */
    public Task getTask(int index) {
        // Callers use a zero-based index that must refer to an existing task.
        assert index >= 0 && index < tasks.size() : "Task index must be valid";
        return this.tasks.get(index);
    }

    /** Returns whether this list contains no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns tasks whose descriptions contain the supplied keyword, ignoring case.
     *
     * @param keyword text to search for in task descriptions
     * @return matching tasks in their existing list order
     */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /** Returns tasks containing the supplied tag, ignoring tag letter case. */
    public List<Task> findTasksByTag(String tag) {
        String normalizedTag = Task.normalizeTag(tag);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.hasTag(normalizedTag)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
}
