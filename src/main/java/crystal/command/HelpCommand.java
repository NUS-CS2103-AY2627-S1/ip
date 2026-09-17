package crystal.command;

import crystal.storage.Storage;
import crystal.task.TaskList;
import crystal.ui.Ui;

/**
 * Displays the complete command guide without changing application state.
 */
public final class HelpCommand extends Command {
    /**
     * Creates a command that displays Crystal's command guide.
     */
    public HelpCommand() {
    }

    /**
     * Displays the command guide without changing tasks or storage.
     *
     * @param tasks task list; not used by this command.
     * @param ui interface used to present the command guide.
     * @param storage persistence service; not used by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
