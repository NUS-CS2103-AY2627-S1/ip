package rudra.command;

import java.util.ArrayList;
import java.util.List;

import rudra.exception.RudraException;
import rudra.storage.Storage;
import rudra.task.Task;
import rudra.ui.Ui;

/**
 * Represents one user command that can be executed against the chatbot state.
 */
public abstract class Command {
    /**
     * Executes the command against the current task list and collaborators.
     *
     * @param tasks Current task list.
     * @param ui UI helper used to show output.
     * @param storage Storage helper used to persist changes.
     * @throws RudraException If the command cannot be completed.
     */
    public abstract void execute(ArrayList<Task> tasks, Ui ui, Storage storage) throws RudraException;

    /**
     * Returns whether running this command should end the chatbot session.
     *
     * @return True if this command exits the application.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Ensures that a task index refers to an existing task.
     *
     * @param tasks Current task list.
     * @param taskIndex Zero-based index to validate.
     * @throws RudraException If the index is outside the task list.
     */
    protected void validateTaskIndex(List<Task> tasks, int taskIndex) throws RudraException {
        if (taskIndex >= tasks.size()) {
            throw new RudraException("That task number is out of range.");
        }
    }

    /**
     * Adds a task and rolls back the change if saving fails.
     *
     * @param tasks Current task list.
     * @param task Task to add.
     * @param storage Storage helper used to persist the list.
     * @throws RudraException If saving fails.
     */
    protected void addTask(ArrayList<Task> tasks, Task task, Storage storage) throws RudraException {
        assert tasks != null : "The task list must be initialized before executing a command";
        assert task != null : "Only a constructed task can be added";
        assert storage != null : "Storage must be initialized before executing a command";

        int originalTaskCount = tasks.size();
        tasks.add(task);
        assert tasks.size() == originalTaskCount + 1 && tasks.get(originalTaskCount) == task
                : "Adding a task must append exactly that task";

        try {
            storage.saveTasks(tasks);
        } catch (RudraException e) {
            tasks.remove(tasks.size() - 1);
            assert tasks.size() == originalTaskCount : "A failed save must roll back the added task";
            throw new RudraException(e.getMessage() + " Your task list was left unchanged.");
        }
    }

    /**
     * Marks or unmarks a task and rolls back the change if saving fails.
     *
     * @param tasks Current task list.
     * @param taskIndex Index of the task to update.
     * @param storage Storage helper used to persist the list.
     * @param shouldMarkAsDone Whether the task should be marked done.
     * @throws RudraException If saving fails.
     */
    protected void updateTaskStatus(ArrayList<Task> tasks, int taskIndex, Storage storage, boolean shouldMarkAsDone)
            throws RudraException {
        assert tasks != null : "The task list must be initialized before executing a command";
        assert taskIndex >= 0 && taskIndex < tasks.size() : "A validated task index must refer to an existing task";
        assert storage != null : "Storage must be initialized before executing a command";

        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();

        if (shouldMarkAsDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        assert task.isDone() == shouldMarkAsDone : "The task status must match the requested update";

        try {
            storage.saveTasks(tasks);
        } catch (RudraException e) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            assert task.isDone() == wasDone : "A failed save must restore the original task status";
            throw new RudraException(e.getMessage() + " Your task list was left unchanged.");
        }
    }

    /**
     * Deletes a task and restores it if saving fails.
     *
     * @param tasks Current task list.
     * @param taskIndex Index of the task to delete.
     * @param storage Storage helper used to persist the list.
     * @return Removed task.
     * @throws RudraException If saving fails.
     */
    protected Task deleteTask(ArrayList<Task> tasks, int taskIndex, Storage storage) throws RudraException {
        assert tasks != null : "The task list must be initialized before executing a command";
        assert taskIndex >= 0 && taskIndex < tasks.size() : "A validated task index must refer to an existing task";
        assert storage != null : "Storage must be initialized before executing a command";

        int originalTaskCount = tasks.size();
        Task removedTask = tasks.remove(taskIndex);
        assert tasks.size() == originalTaskCount - 1 : "Deleting a task must remove exactly one task";

        try {
            storage.saveTasks(tasks);
            return removedTask;
        } catch (RudraException e) {
            tasks.add(taskIndex, removedTask);
            assert tasks.size() == originalTaskCount && tasks.get(taskIndex) == removedTask
                    : "A failed save must restore the deleted task at its original position";
            throw new RudraException(e.getMessage() + " Your task list was left unchanged.");
        }
    }
}
