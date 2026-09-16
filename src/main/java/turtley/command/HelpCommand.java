package turtley.command;

import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Displays Turtley's supported commands and tag rules.
 */
public class HelpCommand extends Command {

    /**
     * Creates a help command.
     */
    public HelpCommand() {
    }

    /**
     * Displays the command reference.
     *
     * @param tasks the application's task list, which is not modified.
     * @param ui the application's user interface.
     * @param storage the application's persistence service, which is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
