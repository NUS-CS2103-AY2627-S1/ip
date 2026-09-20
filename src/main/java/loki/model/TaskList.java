package loki.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import loki.exception.LokiExceptions;
import loki.parser.TaskUpdate;

/**
 * Owns the application's tasks and operations on their one-based indices.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks = new ArrayList<>();

    /** Creates an empty task list. */
    public TaskList() {
    }

    /**
     * Adds a non-null task to the end of the list.
     *
     * @param task the task to add
     * @throws IllegalArgumentException if {@code task} is {@code null}
     */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        tasks.add(task);
    }

    /**
     * Adds every task from another collection.
     *
     * @param otherTasks the tasks to add
     * @throws IllegalArgumentException if {@code otherTasks} is {@code null}
     */
    public void addAll(Iterable<Task> otherTasks) {
        if (otherTasks == null) {
            throw new IllegalArgumentException("Tasks cannot be null");
        }
        for (Task task : otherTasks) {
            add(task);
        }
    }

    /**
     * Returns a task using a one-based index.
     *
     * @param oneBasedIndex the one-based position of the task
     * @return the task at the requested position
     * @throws LokiExceptions if the index is outside the list
     */
    public Task get(int oneBasedIndex) throws LokiExceptions {
        validateIndex(oneBasedIndex);
        return tasks.get(oneBasedIndex - 1);
    }

    /**
     * Removes and returns a task using a one-based index.
     *
     * @param oneBasedIndex the one-based position of the task to remove
     * @return the removed task
     * @throws LokiExceptions if the index is outside the list
     */
    public Task delete(int oneBasedIndex) throws LokiExceptions {
        validateIndex(oneBasedIndex);
        return tasks.remove(oneBasedIndex - 1);
    }

    /**
     * Marks a task complete using a one-based index.
     *
     * @param oneBasedIndex the one-based position of the task
     * @return the marked task
     * @throws LokiExceptions if the index is outside the list
     */
    public Task mark(int oneBasedIndex) throws LokiExceptions {
        Task task = get(oneBasedIndex);
        task.markDone();
        // Every Task implementation must honor the completion contract before we report success.
        assert task.isDone() : "Marking a task must leave it completed";
        return task;
    }

    /**
     * Marks a task incomplete using a one-based index.
     *
     * @param oneBasedIndex the one-based position of the task
     * @return the unmarked task
     * @throws LokiExceptions if the index is outside the list
     */
    public Task unmark(int oneBasedIndex) throws LokiExceptions {
        Task task = get(oneBasedIndex);
        task.unmarkDone();
        // Repeated unmarking must also leave the task incomplete.
        assert !task.isDone() : "Unmarking a task must leave it incomplete";
        return task;
    }

    /**
     * Atomically replaces the selected task with its updated version.
     *
     * @param update the validated update to apply.
     * @return the replacement task.
     * @throws LokiExceptions if the task number is outside the list.
     * @throws IllegalArgumentException if the update does not apply to the task type.
     */
    public Task update(TaskUpdate update) throws LokiExceptions {
        if (update == null) {
            throw new IllegalArgumentException("Task update cannot be null");
        }
        int taskNumber = update.getTaskNumber();
        Task currentTask = get(taskNumber);
        Task replacement = currentTask.updatedWith(update);
        tasks.set(taskNumber - 1, replacement);
        return replacement;
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether this list contains no tasks.
     *
     * @return true if the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns a read-only view for code that only needs to inspect tasks.
     *
     * @return an unmodifiable view of the tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns an iterator over the tasks in list order.
     *
     * @return an iterator over the tasks
     */
    @Override
    public Iterator<Task> iterator() {
        return asList().iterator();
    }

    /**
     * Checks that a one-based index refers to an existing task.
     *
     * @param oneBasedIndex the index to validate
     * @throws LokiExceptions if the index is outside the list
     */
    private void validateIndex(int oneBasedIndex) throws LokiExceptions {
        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size()) {
            throw LokiExceptions.invalidTaskNumber();
        }
    }
}
