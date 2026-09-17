package megatron.command;

import megatron.exception.MegatronException;
import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    protected Command() { }

    /**
     * Executes this command using the application objects.
     *
     * @param tasks The current task list.
     * @param ui The user interface used for output.
     * @param storage The task storage used for persistence.
     * @throws MegatronException If command input or task selection is invalid.
     */
    public abstract void execute(TaskList tasks, Ui ui, TaskStorage storage) throws MegatronException;

    /**
     * Returns whether executing this command should end the application.
     *
     * @return True if the application should end.
     */
    public boolean isExit() {
        return false;
    }
}
