package arrodes.command;

import arrodes.exception.ArrodesException;
import arrodes.storage.Storage;
import arrodes.task.Task;
import arrodes.task.TaskList;
import arrodes.ui.Ui;

/** Represents one parsed user command. */
public abstract class Command {
    /** Creates a command. */
    public Command() {
    }

    /**
     * Returns whether executing this command should terminate Arrodes.
     * @return {@code true} only for commands that exit the application
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Adds a task, saves the updated list, and reports the new task.
     *
     * <p>The task list is restored if saving fails, so a failed command does not
     * leave the in-memory state different from the persisted state.</p>
     *
     * @param task task to add
     * @param ui interface used for user-facing output
     * @param taskList list to update
     * @param storage persistence layer
     * @throws ArrodesException if the task cannot be added or saved
     */
    protected final void addTaskAndSave(Task task, Ui ui, TaskList taskList, Storage storage)
            throws ArrodesException {
        taskList.insert(task);
        try {
            storage.save(taskList);
        } catch (ArrodesException exception) {
            taskList.delete(taskList.getSize());
            throw exception;
        }
        ui.showMessage(formatAddedTaskMessage(task, taskList.getSize()));
    }

    /** Formats the common confirmation shown after a task is added. */
    private String formatAddedTaskMessage(Task task, int taskCount) {
        return "Your request has been inscribed in Arrodes' annals:\n"
                + "   " + task + "\n"
                + taskCount + " tasks are being tracked";
    }

    /**
     * Applies this command to the current task list and persistence layer.
     * @param ui interface used for user-facing output
     * @param taskList current tasks
     * @param storage persistence layer for saving changes
     */
    public abstract void execute(Ui ui, TaskList taskList, Storage storage);
}
