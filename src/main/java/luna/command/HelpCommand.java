package luna.command;

import luna.storage.Storage;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Represents a command that displays Luna's command reference.
 */
public class HelpCommand extends Command {
    /**
     * Displays the available commands without changing task data.
     *
     * @param tasks Current tasks in memory.
     * @param ui User interface for showing the command reference.
     * @param storage Storage used by other commands.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
