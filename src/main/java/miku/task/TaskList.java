package miku.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Owns Miku's ordered collection of tasks and its basic list operations. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Creates a task list containing a copy of the supplied tasks. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Returns the task at a zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes and returns the task at a zero-based index. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Replaces and returns the task at a zero-based index. */
    public Task replace(int index, Task task) {
        return tasks.set(index, task);
    }

    /** Returns the current number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns an immutable snapshot for collaborators that only need to read tasks. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /** Returns tasks whose descriptions contain the given keyword, regardless of letter case. */
    public List<Task> findByDescription(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }
}
