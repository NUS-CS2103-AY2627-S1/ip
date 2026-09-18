package duck.command;

import duck.DuckException;
import duck.storage.Storage;
import duck.task.Task;
import duck.task.TaskList;
import duck.ui.Ui;

/**
 * Provides the shared execution flow for commands that change a task's status.
 */
public abstract class TaskStatusCommand extends Command {
    /** One-based task number supplied by the user. */
    private final int taskNumber;

    /** Status to apply when this command executes. */
    private final boolean shouldMarkAsDone;

    /**
     * Creates a command that applies the requested status to one task.
     *
     * @param taskNumber One-based task number.
     * @param shouldMarkAsDone True to mark the task done, or false to mark it undone.
     */
    protected TaskStatusCommand(int taskNumber, boolean shouldMarkAsDone) {
        this.taskNumber = taskNumber;
        this.shouldMarkAsDone = shouldMarkAsDone;
    }

    /**
     * Changes and saves the status, restoring the original status if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuckException {
        if (this.taskNumber < 1 || this.taskNumber > tasks.getTaskCount()) {
            throw new DuckException("That task number does not exist. "
                    + "Use list to check the available task numbers.");
        }

        int taskIndex = this.taskNumber - 1;
        Task task = tasks.get(taskIndex);
        if (task.isDone() == this.shouldMarkAsDone) {
            String existingStatus = this.shouldMarkAsDone ? "complete" : "active";
            throw new DuckException(
                    "Task " + this.taskNumber + " is already " + existingStatus + ".");
        }

        boolean wasDone = task.isDone();
        setTaskStatus(tasks, taskIndex, this.shouldMarkAsDone);
        assert task.isDone() == this.shouldMarkAsDone
                : "Task status was not updated correctly.";

        try {
            storage.save(tasks.getTasksSnapshot());
        } catch (DuckException e) {
            setTaskStatus(tasks, taskIndex, wasDone);
            throw e;
        }
        showConfirmation(ui, task);
    }

    /** Applies a status through TaskList so the collection remains the mutation owner. */
    private void setTaskStatus(TaskList tasks, int taskIndex, boolean isDone) {
        if (isDone) {
            tasks.markAsDone(taskIndex);
        } else {
            tasks.markAsUndone(taskIndex);
        }
    }

    /**
     * Displays the confirmation specific to the concrete status command.
     *
     * @param ui User interface used to display the confirmation.
     * @param task Task whose status changed.
     */
    protected abstract void showConfirmation(Ui ui, Task task);
}
