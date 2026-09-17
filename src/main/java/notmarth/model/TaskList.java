package notmarth.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import notmarth.exception.NotMarthException;

/**
 * Stores the tasks in their display order and provides task-list operations.
 */
public final class TaskList implements Iterable<Task> {
    private final int maximumTasks;
    private final ArrayList<Task> tasks;

    /**
     * Creates a task list from zero or more initial tasks.
     *
     * @param maximumTasks the largest number of tasks this list can contain.
     * @param loadedTasks the tasks to place in the list initially.
     */
    public TaskList(int maximumTasks, Task... loadedTasks) {
        this(List.of(loadedTasks), maximumTasks);
    }

    /**
     * Creates a task list from tasks loaded at startup.
     *
     * @param loadedTasks the tasks recovered from storage.
     * @param maximumTasks the largest number of tasks this list can contain.
     */
    public TaskList(List<Task> loadedTasks, int maximumTasks) {
        // Loading and construction both provide a concrete collection of tasks.
        if (loadedTasks == null) {
            throw new IllegalArgumentException("A task list must be constructed with a task collection.");
        }
        if (maximumTasks < 1) {
            throw new IllegalArgumentException("The maximum number of tasks must be positive.");
        }
        if (loadedTasks.size() > maximumTasks) {
            throw new IllegalArgumentException("The loaded task list is too large.");
        }
        this.maximumTasks = maximumTasks;
        this.tasks = new ArrayList<>(loadedTasks);
        if (tasks.stream().anyMatch(task -> task == null)) {
            throw new IllegalArgumentException("A task list cannot contain null tasks.");
        }
        if (hasDuplicateTasks()) {
            throw new IllegalArgumentException("A task list cannot contain duplicate tasks.");
        }
        // The public checks above establish this invariant for every new task list.
        assert this.tasks.size() <= this.maximumTasks
                : "Loaded tasks must fit within the task-list capacity";
    }

    /**
     * Adds a task if the list still has room.
     *
     * @param task the task to add.
     * @throws NotMarthException if the task list is full.
     */
    public void add(Task task) throws NotMarthException {
        // Null tasks would break display, persistence, and task-type operations.
        if (task == null) {
            throw new IllegalArgumentException("A task list cannot contain a null task.");
        }
        if (tasks.size() == maximumTasks) {
            throw new NotMarthException(
                    "Your task list is full. Remove a task before adding another one.");
        }
        if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetailsAs(task))) {
            throw new NotMarthException("That task is already in your battle plan.");
        }
        tasks.add(task);
        // Adding a task should increase the list by one and place it at the end.
        assert tasks.get(tasks.size() - 1) == task : "A newly added task must be last in the task list";
    }

    /**
     * Marks a task as complete.
     *
     * @param taskNumber the one-based task number.
     * @return the task that was marked.
     * @throws NotMarthException if the task number is invalid.
     */
    public Task mark(int taskNumber) throws NotMarthException {
        Task task = requireTask(taskNumber, "marking");
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as incomplete.
     *
     * @param taskNumber the one-based task number.
     * @return the task that was unmarked.
     * @throws NotMarthException if the task number is invalid.
     */
    public Task unmark(int taskNumber) throws NotMarthException {
        Task task = requireTask(taskNumber, "unmarking");
        task.markAsUndone();
        return task;
    }

    /**
     * Removes a task and closes the numbering gap left behind.
     *
     * @param taskNumber the one-based task number.
     * @return the removed task.
     * @throws NotMarthException if the task number is invalid.
     */
    public Task delete(int taskNumber) throws NotMarthException {
        requireTask(taskNumber, "deleting");
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns a task by its zero-based position for display and date filtering.
     *
     * @param index the zero-based position.
     * @return the task at that position.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return the current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether this list has no tasks.
     *
     * @return {@code true} when no tasks are stored.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Exposes a read-only view for persistence without exposing list mutation.
     *
     * @return the tasks in their current order.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    private Task requireTask(int taskNumber, String action) throws NotMarthException {
        if (tasks.isEmpty()) {
            throw new NotMarthException("There are no tasks yet. Add a task before " + action + " it.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new NotMarthException(
                    "That task number is not in your list. Use a number from 1 to " + tasks.size() + ".");
        }
        // The checks above guarantee that this one-based number resolves to a task.
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "A validated task number must refer to a stored task";
        return tasks.get(taskNumber - 1);
    }

    private boolean hasDuplicateTasks() {
        for (int i = 0; i < tasks.size(); i++) {
            for (int j = i + 1; j < tasks.size(); j++) {
                if (tasks.get(i).hasSameDetailsAs(tasks.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a read-only iterator over tasks in display order.
     *
     * @return an iterator that cannot modify this task list.
     */
    @Override
    public java.util.Iterator<Task> iterator() {
        return asList().iterator();
    }
}
