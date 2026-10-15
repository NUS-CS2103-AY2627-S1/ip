package orbit.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns Orbit's task collection and provides operations that modify it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing previously loaded tasks.
     *
     * @param tasks tasks loaded from storage
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a one-based task number.
     *
     * @param taskNumber one-based task number
     * @return removed task
     */
    public Task delete(int taskNumber) {
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Marks the task at a one-based task number as done.
     *
     * @param taskNumber one-based task number
     * @return updated task display text
     */
    public String mark(int taskNumber) {
        return tasks.get(taskNumber - 1).mark();
    }

    /**
     * Marks the task at a one-based task number as not done.
     *
     * @param taskNumber one-based task number
     * @return updated task display text
     */
    public String unmark(int taskNumber) {
        return tasks.get(taskNumber - 1).unmark();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword text to search for in task descriptions
     * @return matching tasks in their original list order
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Finds dated tasks that occur on a given date.
     *
     * @param date date whose schedule is being viewed
     * @return scheduled tasks in their original list order
     */
    public List<Task> getSchedule(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .toList();
    }

    /**
     * Returns a read-only view of the tasks for display and storage.
     *
     * @return unmodifiable task view
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
