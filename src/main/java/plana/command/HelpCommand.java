package plana.command;

import plana.storage.Storage;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Displays Plana's list of available commands.
 */
public class HelpCommand extends Command {
    /**
     * Displays the help text and the separator following it.
     *
     * @param tasks unused by this command.
     * @param ui the user interface used for the help response.
     * @param storage unused by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
        ui.showLine();
    }
}
