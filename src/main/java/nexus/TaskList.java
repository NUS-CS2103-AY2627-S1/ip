package nexus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Owns and manages Nexus's collection of tasks. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("Task list source must not be null.");
        }
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task. */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null.");
        }
        if (tasks.stream().anyMatch(existing -> existing.toString().equals(task.toString()))) {
            throw new IllegalArgumentException("A task with the same details already exists.");
        }
        tasks.add(task);
    }

    /** Returns a task by zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether an index identifies a task. */
    public boolean hasIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /** Marks a task as done. */
    public void mark(int index) {
        tasks.get(index).markAsDone();
    }

    /** Marks a task as not done. */
    public void unmark(int index) {
        tasks.get(index).markAsNotDone();
    }

    /** Deletes and returns a task. */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /** Returns a snapshot of the tasks. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /** Returns tasks that are not snoozed on the supplied date. */
    public List<Task> visibleTasks(LocalDate date) {
        return tasks.stream()
                .filter(task -> !task.isSnoozed(date))
                .toList();
    }

    /** Snoozes a task until the supplied date. */
    public void snooze(int index, LocalDate date) {
        tasks.get(index).snoozeUntil(date);
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring letter case. */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(Collectors.toList());
    }
}
