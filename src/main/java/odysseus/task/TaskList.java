package odysseus.task;

import odysseus.OdysseusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles all TaskList operations: modifying of list, task status
 * and checks on task.
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Creates a tasklist object with empty list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a tasklist object with an initial task list.
     *
     * @param initial the initial list of tasks to populate this list with
     */
    public TaskList(List<Task> initial) {
        assert initial != null : "initial is null";
        this.tasks = initial;
    }

    /**
     * Adds task into tasklist.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Returns a removed task.
     *
     * @param idx index of task to remove using 0-base indexing
     * @return removed task
     */
    public Task remove(int idx) {
        assert idx >= 0 && idx < tasks.size() : "remove called with out-of-range index: " + idx;
        return this.tasks.remove(idx);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks in the list
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns task marked as done.
     *
     * @param idx index of task using 0-base indexing
     * @return task marked as done
     */
    public Task mark(int idx) {
        assert idx >= 0 && idx < tasks.size() : "mark called with out-of-range index: " + idx;
        Task task = this.tasks.get(idx);
        task.markAsDone();
        return task;
    }

    /**
     * Returns task marked as undone.
     *
     * @param idx index of task using 0-base indexing
     * @return task marked as undone
     */
    public Task unmark(int idx) {
        assert idx >= 0 && idx < tasks.size() : "unmark called with out-of-range index: " + idx;
        Task task = this.tasks.get(idx);
        task.markAsUndone();
        return task;
    }

    /**
     * Returns unmodifiable list of tasks.
     *
     * @return list of tasks
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns list of task due on a date.
     *
     * @param date the date the task is due on
     * @return list of tasks that occurs on given date
     */
    public List<Task> on(LocalDate date) {
        assert date != null : "on called with a null date";
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .toList();
    }

    /**
     * Returns task list with keyword in description.
     *
     * @param keyword string to check description against
     * @return list of tasks with description containing string
     */
    public List<Task> find(String keyword) {
        assert keyword != null : "find keyword is null";
        return tasks.stream()
                .filter(task -> task.descriptionContains(keyword))
                .toList();
    }

    /**
     * Returns whether the list already contains a task equivalent to the given
     * one, as judged by {@link Task#isSameTask(Task)}.
     *
     * @param task the candidate task to check for
     * @return true if an equivalent task is already present
     */
    public boolean hasDuplicate(Task task) {
        assert task != null : "hasDuplicate called with a null task";
        return tasks.stream()
                .anyMatch(task::isSameTask);
    }

    /**
     * Updates a field of the task at the given index.
     *
     * @param idx   index of the task using 0-based indexing
     * @param flag  the field to update (e.g. {@code /desc}, {@code /by})
     * @param value the new value for that field
     * @return the updated task
     * @throws OdysseusException if the flag is not applicable to the task type
     */
    public Task update(int idx, String flag, String value) throws OdysseusException {
        assert idx >= 0 && idx < tasks.size() : "update called with out-of-range index: " + idx;
        Task task = this.tasks.get(idx);
        task.update(flag, value);
        return task;
    }

}
