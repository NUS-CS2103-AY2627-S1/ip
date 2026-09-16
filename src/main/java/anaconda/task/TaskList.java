package anaconda.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import anaconda.exception.AnacondaException;
import anaconda.parser.Command;

/**
 * Owns the task collection and provides task operations independently of input or storage.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Captures task order and completion flags without exposing mutable snapshot contents.
     * Descriptions and dates are immutable, so retaining the original task objects preserves their types.
     */
    public static final class Snapshot {
        private final List<TaskState> taskStates;

        private Snapshot(List<TaskState> taskStates) {
            this.taskStates = taskStates;
        }
    }

    /**
     * Retains a task and its completion flag at the time a snapshot is taken.
     */
    private record TaskState(Task task, boolean isDone) {
    }

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing previously loaded tasks in their saved order.
     *
     * @param initialTasks Tasks to copy into the collection.
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task at the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Checks for the same type, case-insensitive description, and dates, ignoring completion status.
     *
     * @param candidate Task to compare with the current list.
     * @return Whether an existing task matches without changing the list.
     */
    public boolean hasDuplicate(Task candidate) {
        return tasks.stream().anyMatch(task -> task.getClass() == candidate.getClass()
                && task.getDescription().equalsIgnoreCase(candidate.getDescription())
                && Objects.equals(task.getEndDate(), candidate.getEndDate())
                && (!(task instanceof Event event) || event.getFrom().equals(((Event) candidate).getFrom())));
    }

    /**
     * Deletes a task using the one-based number shown to the user.
     *
     * @param taskNumber Task number to remove.
     * @return Removed task.
     * @throws AnacondaException If the task does not exist.
     */
    public Task delete(int taskNumber) throws AnacondaException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Changes a task's completion status.
     *
     * @param taskNumber One-based task number.
     * @param isDone Whether the task should be completed.
     * @return Updated task.
     * @throws AnacondaException If the task does not exist.
     */
    public Task mark(int taskNumber, boolean isDone) throws AnacondaException {
        Task task = tasks.get(toIndex(taskNumber));
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
        // Every task subtype must honor the status update before the caller reports success.
        assert task.isDone() == isDone : "Task completion status must match the requested state";
        return task;
    }

    /**
     * Removes every task from the list.
     */
    public void clear() {
        tasks.clear();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a structurally unmodifiable snapshot for display or saving.
     * The task objects themselves are shared, not copied.
     *
     * @return Tasks in their current order.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Captures task order and completion flags for later restoration.
     *
     * @return Snapshot unaffected by later additions, removals, or completion updates.
     */
    public Snapshot snapshot() {
        return new Snapshot(tasks.stream()
                .map(task -> new TaskState(task, task.isDone()))
                .toList());
    }

    /**
     * Restores the task objects, order, and completion flags captured by a snapshot.
     *
     * @param snapshot Previously captured state to restore.
     */
    public void restore(Snapshot snapshot) {
        tasks.clear();
        for (TaskState state : snapshot.taskStates) {
            if (state.isDone()) {
                state.task().markAsDone();
            } else {
                state.task().markAsUndone();
            }
            tasks.add(state.task());
        }
    }

    /**
     * Finds tasks whose descriptions contain the supplied keyword, ignoring case.
     *
     * @param keyword Text to search for in task descriptions.
     * @return Unmodifiable snapshot of matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Finds dated tasks by their deadline or event ending date without changing the list.
     *
     * @param filterDate Date to compare against.
     * @param direction BY for on-or-before, or FROM for on-or-after.
     * @param isSharp Whether only exact date matches should be returned.
     * @return Unmodifiable snapshot of matching tasks in their original order.
     * @throws IllegalArgumentException If the direction is not BY or FROM.
     */
    public List<Task> filterByDate(LocalDate filterDate, Command direction, boolean isSharp) {
        if (direction != Command.BY && direction != Command.FROM) {
            throw new IllegalArgumentException("Command does not filter by date: " + direction);
        }
        return tasks.stream()
                .filter(task -> matchesDateFilter(task.getEndDate(), filterDate, direction, isSharp))
                .toList();
    }

    /**
     * Tests a task's ending date against the filter, excluding undated tasks.
     * Sharp filters require equality; other filters include the boundary date.
     */
    private boolean matchesDateFilter(LocalDate endDate, LocalDate filterDate, Command direction, boolean isSharp) {
        if (endDate == null) {
            return false;
        }
        if (isSharp) {
            return endDate.equals(filterDate);
        }
        if (direction == Command.BY) {
            return !endDate.isAfter(filterDate);
        }
        return !endDate.isBefore(filterDate);
    }

    /**
     * Validates a user-facing task number before converting it to a list index.
     */
    private int toIndex(int taskNumber) throws AnacondaException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new AnacondaException("Task " + taskNumber + " does not exist.");
        }
        int index = taskNumber - 1;
        // Once user input is validated, conversion must produce a safe zero-based index.
        assert index >= 0 && index < tasks.size() : "Validated task number must map to an existing index";
        return index;
    }

}
