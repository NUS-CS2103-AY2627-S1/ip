package rocky.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The user's tasks, in the order they were added.
 */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Adds the given tasks to the end of the list, e.g. ones loaded from disk.
     *
     * @param newTasks the tasks to add, in order.
     */
    public void addAll(List<Task> newTasks) {
        tasks.addAll(newTasks);
    }

    /**
     * Adds the task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given position.
     *
     * @param index the task's position, counting from 0.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index the task's position, counting from 0.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns how many tasks the list has. */
    public int size() {
        return tasks.size();
    }

    /** Returns true if the list has no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks whose description contains the keyword, ignoring
     * case, in list order.
     *
     * @param keyword the text to look for, e.g. "book".
     */
    public List<Task> find(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowerKeyword)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /** Returns a read-only view of every task, in list order. */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
