package greenchonk.command;

import greenchonk.storage.Storage;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Displays guidance for every supported command.
 */
public class HelpCommand extends Command {
    /**
     * Creates a command that displays usage guidance.
     */
    public HelpCommand() {
    }

    /**
     * Displays command guidance without changing or persisting the task list.
     *
     * @param tasks the unused task list.
     * @param ui the UI that displays the guidance.
     * @param storage the unused task storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
