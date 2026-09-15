package chatty.task;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import chatty.exception.ChattyException;

/**
 * Owns the task collection and provides operations that modify it.
 */
public class TaskList {
    /** Tasks currently managed by Chatty. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks with which to initialize the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns a read-only view of the tasks for display and storage.
     *
     * @return unmodifiable task view.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword keyword to find in task descriptions.
     * @return matching tasks in their existing order.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * Completes the selected task or advances a recurring task to its next occurrence.
     *
     * @param taskNumber one-based number of the task to mark.
     * @return task that was completed or advanced.
     * @throws ChattyException if the task number is invalid or a schedule cannot advance.
     */
    public Task mark(int taskNumber) throws ChattyException {
        Task task = getTask(taskNumber);
        if (task instanceof RecurringTask recurringTask) {
            try {
                recurringTask.advanceToNextOccurrence();
            } catch (DateTimeException | ArithmeticException exception) {
                throw new ChattyException("OOPS!!! This recurring task cannot advance beyond "
                        + "the supported date range.");
            }
        } else {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Marks the selected task as not done and returns it for display.
     *
     * @param taskNumber one-based number of the task to unmark.
     * @return task that was unmarked.
     * @throws ChattyException if the task number is invalid.
     */
    public Task unmark(int taskNumber) throws ChattyException {
        Task task = getTask(taskNumber);
        if (task instanceof RecurringTask) {
            throw new ChattyException("OOPS!!! A recurring task always represents its next "
                    + "pending occurrence, so it cannot be unmarked.");
        }
        task.markAsNotDone();
        return task;
    }

    /**
     * Removes and returns the selected task.
     *
     * @param taskNumber one-based number of the task to remove.
     * @return task that was removed.
     * @throws ChattyException if the task number is invalid.
     */
    public Task delete(int taskNumber) throws ChattyException {
        Task task = getTask(taskNumber);
        tasks.remove(taskNumber - 1);
        return task;
    }

    /**
     * Returns the task selected by its one-based task number.
     *
     * @param taskNumber one-based number of the task to select.
     * @return selected task.
     * @throws ChattyException if the task list is empty or the number is invalid.
     */
    private Task getTask(int taskNumber) throws ChattyException {
        if (tasks.isEmpty()) {
            throw new ChattyException("OOPS!!! Your task list is empty.");
        } else if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ChattyException("OOPS!!! Task " + taskNumber + " does not exist. "
                    + "Choose a number from 1 to " + tasks.size() + ".");
        }
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Validated task number must map to an existing task";
        return tasks.get(taskIndex);
    }
}
