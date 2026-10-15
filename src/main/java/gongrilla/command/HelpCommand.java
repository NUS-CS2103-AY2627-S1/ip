package gongrilla.command;

import gongrilla.storage.Storage;
import gongrilla.task.TaskList;
import gongrilla.ui.Ui;

/** Displays the available commands without changing tasks or saved data. */
public class HelpCommand extends Command {
    /** Creates a command that displays usage instructions. */
    public HelpCommand() {
    }

    /**
     * Displays the command reference.
     *
     * @param tasks task list; unused because help does not change tasks.
     * @param ui user interface used to display help.
     * @param storage task storage; unused because help does not save changes.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
