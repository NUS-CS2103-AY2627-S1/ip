package vector.command;

import vector.storage.Storage;
import vector.task.TaskList;
import vector.ui.Ui;

/**
 * Represents a command to exit the application.
 */
public class ExitCommand extends Command {
    /**
     * Executes the command, displaying a goodbye message.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The user interface for displaying messages.
     * @param storage The storage for saving updates.
     * @return The response string.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return "System shutting down. End of line.";
    }

    /**
     * Determines whether this command should exit the application.
     *
     * @return true, as this is the exit command.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
