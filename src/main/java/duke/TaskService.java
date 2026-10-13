package duke;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates task operations that are shared by Wangsa's user interfaces.
 *
 * <p>Changes are saved before an interface reports success. If a save fails, the
 * service restores the task state so the screen and saved data stay in sync.</p>
 */
public final class TaskService {
    private final Parser parser;

    private final TaskRepository repository;

    private final TaskList tasks;

    /**
     * Creates a service and loads its task list from storage.
     *
     * @param repository Source and destination for saved tasks.
     * @param parser Parser for Wangsa commands.
     * @throws StorageException If saved tasks cannot be loaded.
     * @throws WangsaException If the saved task list is invalid.
     */
    public TaskService(TaskRepository repository, Parser parser) throws StorageException, WangsaException {
        this.parser = parser;
        this.repository = repository;
        this.tasks = new TaskList(repository.loadTasks());
    }

    /**
     * Returns the current task order, using the same task objects as the service.
     *
     * @return An unmodifiable copy of the task order.
     */
    public List<Task> getTasks() {
        return tasks.getTasks();
    }

    /**
     * Finds tasks matching the keyword in a complete {@code find} command.
     *
     * @param command Complete find command.
     * @return Matching tasks in their original order.
     * @throws WangsaException If the search keyword is missing.
     */
    public List<Task> find(String command) throws WangsaException {
        return tasks.find(parser.parseSearchKeyword(command));
    }

    /**
     * Finds tasks by a validated keyword supplied by a parsed command.
     *
     * @param keyword Search keyword.
     * @return Matching tasks in their original order.
     */
    public List<Task> findKeyword(String keyword) {
        return tasks.find(keyword);
    }

    /**
     * Finds tasks with the same task numbers used by status and delete commands.
     *
     * @param keyword Validated search keyword.
     * @return Matching tasks with full-list positions.
     */
    public List<TaskMatch> findMatches(String keyword) {
        return tasks.findMatches(keyword);
    }

    /**
     * Sorts tasks by deadline and saves the resulting order.
     *
     * @throws StorageException If the updated order cannot be saved.
     */
    public void sortByDeadline() throws StorageException {
        List<Task> previousOrder = tasks.getTasks();
        tasks.sortByDeadline();
        try {
            repository.saveTasks(tasks.getTasks());
        } catch (StorageException exception) {
            tasks.restoreOrder(previousOrder);
            throw exception;
        }
    }

    /**
     * Adds a task described by the command and saves it.
     *
     * @param command Complete task-creation command.
     * @return The added task.
     * @throws WangsaException If the command or task is invalid.
     * @throws StorageException If the updated list cannot be saved.
     */
    public Task add(String command) throws WangsaException, StorageException {
        return add(parser.parseTask(command));
    }

    /**
     * Adds a validated task and persists it at the end of the current order.
     *
     * @param task Task to add.
     * @return The added task.
     * @throws WangsaException If the list cannot accept the task.
     * @throws StorageException If the task cannot be persisted.
     */
    public Task add(Task task) throws WangsaException, StorageException {
        tasks.add(task);
        try {
            repository.saveTasks(tasks.getTasks());
        } catch (StorageException exception) {
            rollbackAddedTask(exception);
            throw exception;
        }
        return task;
    }

    /**
     * Marks the task identified by a complete {@code mark} command as done and saves it.
     *
     * @param command Complete mark command.
     * @return The updated task.
     * @throws WangsaException If the task number is invalid.
     * @throws StorageException If the updated list cannot be saved.
     */
    public Task mark(String command) throws WangsaException, StorageException {
        return mark(parser.parseTaskNumber(command));
    }

    /**
     * Marks the supplied one-based task number as done and persists the change.
     *
     * @param taskNumber One-based task number.
     * @return The updated task.
     * @throws WangsaException If the task number is invalid.
     * @throws StorageException If the update cannot be persisted.
     */
    public Task mark(int taskNumber) throws WangsaException, StorageException {
        return updateStatus(taskNumber, true);
    }

    /**
     * Marks the task identified by a complete {@code unmark} command as not done and saves it.
     *
     * @param command Complete unmark command.
     * @return The updated task.
     * @throws WangsaException If the task number is invalid.
     * @throws StorageException If the updated list cannot be saved.
     */
    public Task unmark(String command) throws WangsaException, StorageException {
        return unmark(parser.parseTaskNumber(command));
    }

    /**
     * Marks the supplied one-based task number as not done and persists the change.
     *
     * @param taskNumber One-based task number.
     * @return The updated task.
     * @throws WangsaException If the task number is invalid.
     * @throws StorageException If the update cannot be persisted.
     */
    public Task unmark(int taskNumber) throws WangsaException, StorageException {
        return updateStatus(taskNumber, false);
    }

    /**
     * Changes a task's completion state and restores it if persistence fails.
     */
    private Task updateStatus(int taskNumber, boolean shouldMarkAsDone)
            throws WangsaException, StorageException {
        Task task = tasks.get(taskNumber);
        boolean wasDone = task.isDone();
        if (shouldMarkAsDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        try {
            repository.saveTasks(tasks.getTasks());
        } catch (StorageException exception) {
            restoreStatus(task, wasDone);
            throw exception;
        }
        return task;
    }

    /**
     * Deletes the task identified by a complete {@code delete} command and saves the result.
     *
     * @param command Complete delete command.
     * @return The removed task.
     * @throws WangsaException If the task number is invalid.
     * @throws StorageException If the updated list cannot be saved.
     */
    public Task delete(String command) throws WangsaException, StorageException {
        int taskNumber = parser.parseTaskNumber(command);
        return delete(taskNumber);
    }

    /**
     * Deletes the supplied one-based task number and persists the change.
     *
     * @param taskNumber One-based task number.
     * @return The removed task.
     * @throws WangsaException If the task number is invalid.
     * @throws StorageException If the deletion cannot be persisted.
     */
    public Task delete(int taskNumber) throws WangsaException, StorageException {
        Task task = tasks.get(taskNumber);
        List<Task> remainingTasks = new ArrayList<>(tasks.getTasks());
        remainingTasks.remove(taskNumber - 1);
        repository.saveTasks(remainingTasks);
        tasks.delete(taskNumber);
        return task;
    }

    /**
     * Removes an in-memory task after a failed persistence insert.
     */
    private void rollbackAddedTask(StorageException exception) {
        try {
            tasks.delete(tasks.size());
        } catch (WangsaException rollbackException) {
            exception.addSuppressed(rollbackException);
        }
    }

    /**
     * Restores an in-memory status after a failed persistence update.
     */
    private void restoreStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }
}
