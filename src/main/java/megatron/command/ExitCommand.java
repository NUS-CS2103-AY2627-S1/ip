package megatron.command;

import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.ui.Ui;

/**
 * Executes the user's request to exit Megatron.
 */
public final class ExitCommand extends Command {
    /**
     * Creates a command that exits Megatron.
     */
    public ExitCommand() { }

    /**
     * Displays the existing goodbye message.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) {
        ui.showGoodbye();
    }

    /**
     * Returns whether this command ends the application loop.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
