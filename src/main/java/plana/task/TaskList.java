package plana.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import plana.command.TaskAction;
import plana.exception.PlanaException;

/**
 * Owns Plana's ordered collection of tasks and provides the operations used by
 * the command loop.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks the initial tasks.
     */
    public TaskList(List<Task> tasks) {
        // All list entries must be real tasks because commands iterate over and
        // invoke behavior on every entry without null checks.
        assert tasks != null && tasks.stream().allMatch(task -> task != null)
                : "A task list must contain only non-null tasks.";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds tasks to the end of the list in the order supplied.
     *
     * @param tasks the tasks to add.
     */
    public void add(Task... tasks) {
        // Preserve the list invariant for tasks added after construction.
        assert tasks != null : "Tasks to add must not be null.";
        for (Task task : tasks) {
            assert task != null : "A task list must not contain null tasks.";
            this.tasks.add(task);
        }
    }

    /**
     * Inserts a task at a specified zero-based position.
     *
     * @param index the position at which to insert the task.
     * @param task the task to insert.
     */
    public void add(int index, Task task) {
        assert task != null : "A task list must not contain null tasks.";
        tasks.add(index, task);
    }

    /**
     * Checks whether the list already has a task with the same details.
     *
     * @param task the task to look for.
     * @return true when an equivalent task already exists.
     */
    public boolean containsSameTask(Task task) {
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    /**
     * Returns a snapshot of the tasks in their current order.
     *
     * @return an unmodifiable copy of the current tasks.
     */
    public List<Task> copyTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Replaces all tasks with a previously captured snapshot.
     *
     * @param savedTasks the tasks to restore in display order.
     */
    public void restoreTasks(List<Task> savedTasks) {
        assert savedTasks != null && savedTasks.stream().allMatch(task -> task != null)
                : "A task list must contain only non-null tasks.";
        tasks.clear();
        tasks.addAll(savedTasks);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index the zero-based index.
     * @return the task at that index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index the zero-based index.
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Deletes the task selected by a user-facing task number.
     *
     * @param taskNumber the one-based task number entered by the user.
     * @return the deleted task
     * @throws PlanaException if the task number is missing, invalid, or out of range
     */
    public Task delete(String taskNumber) throws PlanaException {
        return remove(getTaskIndex(TaskAction.DELETE, taskNumber));
    }

    /**
     * Marks the task selected by a user-facing task number as done.
     *
     * @param taskNumber the one-based task number entered by the user.
     * @return the marked task
     * @throws PlanaException if the task number is missing, invalid, or out of range
     */
    public Task mark(String taskNumber) throws PlanaException {
        Task task = getSelectedTask(TaskAction.MARK, taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task selected by a user-facing task number as not done.
     *
     * @param taskNumber the one-based task number entered by the user.
     * @return the unmarked task
     * @throws PlanaException if the task number is missing, invalid, or out of range
     */
    public Task unmark(String taskNumber) throws PlanaException {
        Task task = getSelectedTask(TaskAction.UNMARK, taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Checks the current completion status of a selected task.
     *
     * @param action the command validating the user-facing task number.
     * @param taskNumber the one-based task number entered by the user.
     * @return true when the selected task is complete.
     * @throws PlanaException if the task number is missing, invalid, or out of range.
     */
    public boolean isDone(TaskAction action, String taskNumber) throws PlanaException {
        return getSelectedTask(action, taskNumber).isDone();
    }

    /**
     * Retrieves a task after validating the user-facing task number.
     *
     * @param action the command being performed, used to tailor validation errors.
     * @param taskNumber the one-based task number entered by the user.
     * @return the selected task
     * @throws PlanaException if the task number is missing, invalid, or out of range
     */
    private Task getSelectedTask(TaskAction action, String taskNumber) throws PlanaException {
        return get(getTaskIndex(action, taskNumber));
    }

    /**
     * Converts a valid user-facing task number into a zero-based list index.
     *
     * @param action the command being performed, used to tailor validation errors.
     * @param taskNumber the one-based task number entered by the user.
     * @return the zero-based index of the selected task
     * @throws PlanaException if the task number is missing, invalid, or out of range
     */
    private int getTaskIndex(TaskAction action, String taskNumber) throws PlanaException {
        if (taskNumber.isBlank()) {
            throw new PlanaException("Oops, " + action.getCommandText() + " needs a task number."
                    + " Try: " + action.getCommandText() + " <number>.");
        }

        final int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new PlanaException("Oops, '" + taskNumber + "' isn't a valid task number."
                    + " Use a positive whole number, like " + action.getCommandText() + " 1.");
        }

        if (taskIndex < 0) {
            throw new PlanaException("Oops, task numbers start at 1."
                    + " Try " + action.getCommandText() + " 1 or another number from list.");
        }
        if (taskIndex >= size()) {
            throw new PlanaException("Oops, task " + (taskIndex + 1) + " doesn't exist yet."
                    + " Type list to check the task numbers you have.");
        }
        // The validation above guarantees that the one-based user number has
        // been converted into a safe zero-based index before it is returned.
        assert taskIndex >= 0 && taskIndex < size() : "Validated task index must be in bounds.";
        return taskIndex;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether the list contains no tasks.
     *
     * @return true when the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns an iterator over the tasks in their display order.
     *
     * @return an iterator over this task list
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
