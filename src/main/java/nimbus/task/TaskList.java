package nimbus.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nimbus.NimbusException;

/** Owns the task collection and provides task operations. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Tasks to include initially.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Task collection must not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task. */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /** Returns the task at the supplied one-based number. */
    public Task get(int taskNumber) throws NimbusException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new NimbusException("Choose a task number from the list.");
        }
        return tasks.get(taskNumber - 1);
    }

    /** Removes and returns the task at the supplied one-based number. */
    public Task delete(int taskNumber) throws NimbusException {
        get(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns a read-only view of all tasks. */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring case. */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(normalizedKeyword))
                .toList();
    }
}
