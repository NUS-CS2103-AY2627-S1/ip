package sheppy.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sheppy.SheppyException;

/** Owns Sheppy's collection of tasks and its task-level operations. */
public class TaskList {
    /** The tasks currently tracked by Sheppy. */
    private final List<Task> tasks;

    /**
     * Creates a task list using zero or more supplied tasks.
     *
     * @param initialTasks the tasks to place in the list
     */
    public TaskList(Task... initialTasks) {
        this(List.of(initialTasks));
    }

    /**
     * Creates a task list using the supplied initial tasks.
     *
     * @param initialTasks tasks loaded from storage, if any
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /**
     * Returns a task using the one-based number shown to the user.
     *
     * @param taskNumber the number shown in the task list
     * @return the selected task
     * @throws SheppyException if the number is outside the list
     */
    public Task get(int taskNumber) throws SheppyException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Marks a task done or undone.
     *
     * @param taskNumber the one-based task number
     * @param markDone whether the task should be marked done
     * @return the updated task
     * @throws SheppyException if the number is outside the list
     */
    public Task updateStatus(int taskNumber, boolean markDone) throws SheppyException {
        Task task = get(taskNumber);
        if (markDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
        return task;
    }

    /**
     * Removes a task using the one-based number shown to the user.
     *
     * @param taskNumber the one-based task number
     * @return the removed task
     * @throws SheppyException if the number is outside the list
     */
    public Task remove(int taskNumber) throws SheppyException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only view for display and storage.
     *
     * @return the current tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword the text to search for
     * @return the matching tasks in their original order
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /** Sorts tasks alphabetically by description, ignoring letter case. */
    public void sortByDescription() {
        tasks.sort(Comparator.comparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Task::getDescription));
    }

    /**
     * Captures task membership, order and completion states before a change.
     *
     * @return a snapshot that can restore this list if the change cannot be saved
     */
    public Snapshot snapshot() {
        return new Snapshot();
    }

    /** Remembers a list's mutable state without copying immutable task details. */
    public final class Snapshot {
        private final List<TaskState> states = tasks.stream()
                .map(task -> new TaskState(task, task.isDone())).toList();

        private Snapshot() {
        }

        /** Restores the original order, membership and completion states. */
        public void restore() {
            tasks.clear();
            for (TaskState state : states) {
                if (state.done()) {
                    state.task().markAsDone();
                } else {
                    state.task().markAsUndone();
                }
                tasks.add(state.task());
            }
        }
    }

    /** Captures the only mutable property of a task alongside the task itself. */
    private record TaskState(Task task, boolean done) {
    }

    /** Converts a user-facing one-based number into an internal index. */
    private int toIndex(int taskNumber) throws SheppyException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new SheppyException("that task number is not in your list.");
        }
        int index = taskNumber - 1;
        assert index >= 0 && index < tasks.size() : "Validated task number must map to a valid index";
        return index;
    }
}
