package bibi.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import bibi.BibiException;

/**
 * Stores the tasks managed by Bibi and provides safe indexed access to them.
 */
public class TaskList {
    private final List<Task> tasks;

    /** The state before the latest actual change, or {@code null} when undo is unavailable. */
    private List<TaskState> previousState;

    /**
     * Creates a task list holding the tasks given, in the order given.
     *
     * <p>Varargs, so that both common cases read directly: {@code new TaskList()}
     * for an empty list, and {@code new TaskList(todo, deadline)} where the tasks
     * are known where the list is built, as they are throughout the tests. This
     * replaces a separate no-argument constructor, which it already covers.
     *
     * @param initialTasks the tasks to start with, if any
     */
    public TaskList(Task... initialTasks) {
        tasks = new ArrayList<>(List.of(initialTasks));
    }

    /**
     * Creates a task list holding the supplied tasks, in the given order.
     *
     * <p>Kept alongside the varargs form for callers that already hold a list,
     * such as the one just read from the save file. The tasks are copied, so
     * later changes to this list do not disturb the list handed in.
     *
     * @param initialTasks the tasks to start with
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to store
     */
    public void add(Task task) {
        rememberState();
        tasks.add(task);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Reports whether this list contains no tasks.
     *
     * @return {@code true} when there are no stored tasks
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns every task in list order.
     *
     * <p>The returned list is unmodifiable so callers such as {@link bibi.Storage Storage} can
     * read the tasks without accidentally changing them.
     *
     * @return a read-only view of the stored tasks
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns a task using the one-based number displayed to the user.
     *
     * @param taskNumber one-based task number
     * @return the matching task
     * @throws BibiException if the task number does not exist
     */
    public Task get(int taskNumber) throws BibiException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Removes and returns a task using the one-based number displayed to the user.
     *
     * @param taskNumber one-based task number
     * @return the removed task
     * @throws BibiException if the task number does not exist
     */
    public Task remove(int taskNumber) throws BibiException {
        int taskIndex = toIndex(taskNumber);
        rememberState();
        return tasks.remove(taskIndex);
    }

    /**
     * Marks a task complete and remembers its previous state only when it changes.
     *
     * @param taskNumber one-based task number
     * @return the task concerned, for the command's confirmation
     * @throws BibiException if the task number does not exist
     */
    public Task markComplete(int taskNumber) throws BibiException {
        Task task = get(taskNumber);
        if (!task.isComplete()) {
            rememberState();
            task.markComplete();
        }
        return task;
    }

    /**
     * Reopens a task and remembers its previous state only when it changes.
     *
     * @param taskNumber one-based task number
     * @return the task concerned, for the command's confirmation
     * @throws BibiException if the task number does not exist
     */
    public Task markIncomplete(int taskNumber) throws BibiException {
        Task task = get(taskNumber);
        if (task.isComplete()) {
            rememberState();
            task.markIncomplete();
        }
        return task;
    }

    /**
     * Restores the list before its latest change in this session.
     *
     * <p>Undo is a single recovery step. Restoring a snapshot consumes it, so a
     * second undo cannot redo the change or reach an earlier session.
     *
     * @throws BibiException if no change is available to undo
     */
    public void undo() throws BibiException {
        if (previousState == null) {
            throw new BibiException("There is no change to undo in this session. "
                    + "Make a list change first, then use undo, for example: undo.");
        }
        tasks.clear();
        for (TaskState state : previousState) {
            if (state.isComplete()) {
                state.task().markComplete();
            } else {
                state.task().markIncomplete();
            }
            tasks.add(state.task());
        }
        previousState = null;
    }

    /**
     * Captures order and completion before a mutation replaces the undo step.
     *
     * <p>Task descriptions and dates are immutable, so retaining task references
     * and copying their completion flags preserves the whole list without parsing
     * save-file text or changing the persistence format.
     */
    private void rememberState() {
        previousState = tasks.stream()
                .map(task -> new TaskState(task, task.isComplete()))
                .toList();
    }

    /**
     * Returns the number of an existing task describing the same thing.
     *
     * @param task the task being considered for adding
     * @return the one-based number of the matching task, or 0 when there is none
     */
    public int findSameTaskNumber(Task task) {
        for (int index = 0; index < tasks.size(); index++) {
            if (tasks.get(index).isSameTask(task)) {
                return index + 1;
            }
        }
        return 0;
    }

    /**
     * Reorders this list so the earliest task comes first.
     *
     * <p>Tasks with no date go last rather than first, because a ToDo is
     * something to do at some point and a dated task is something to be ready
     * for; burying the dated ones under the undated ones would defeat sorting.
     *
     * <p>The sort is stable, so tasks that share a moment, and the undated ones
     * at the end, keep the order the user added them in.
     */
    public void sortBySchedule() {
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(TaskList::compareBySchedule);
        if (!tasks.equals(sortedTasks)) {
            rememberState();
            tasks.clear();
            tasks.addAll(sortedTasks);
        }
    }

    /**
     * Orders two tasks by when they are scheduled, putting undated tasks last.
     *
     * @param first the task on the left of the comparison
     * @param second the task on the right of the comparison
     * @return a negative number, zero, or a positive number as the first task
     *     sorts before, with, or after the second
     */
    private static int compareBySchedule(Task first, Task second) {
        Optional<TaskDateTime> firstTime = first.getScheduledTime();
        Optional<TaskDateTime> secondTime = second.getScheduledTime();

        if (firstTime.isEmpty() || secondTime.isEmpty()) {
            // Both undated means neither moves, which stability then preserves.
            return Boolean.compare(firstTime.isEmpty(), secondTime.isEmpty());
        }
        return TaskDateTime.EARLIEST_FIRST.compare(firstTime.get(), secondTime.get());
    }

    /**
     * Converts and validates a displayed one-based task number.
     *
     * @param taskNumber one-based task number
     * @return the matching zero-based list index
     * @throws BibiException if the task number does not exist
     */
    private int toIndex(int taskNumber) throws BibiException {
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            if (tasks.isEmpty()) {
                throw new BibiException("That task number does not exist: the list is empty. "
                        + "Add a task first. Use todo <description>, for example: todo read book.");
            }
            throw new BibiException("That task number does not exist: use a number from 1 to " + tasks.size()
                    + ". Use list to see the current numbers, for example: list.");
        }
        return taskIndex;
    }

    /**
     * Holds a task and its completion flag before the most recent change.
     *
     * @param task the task retained in its original list position
     * @param isComplete the completion flag to restore
     */
    private record TaskState(Task task, boolean isComplete) {
    }
}
