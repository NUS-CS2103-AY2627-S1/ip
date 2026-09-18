package duck.command;

import duck.storage.Storage;
import duck.task.TaskList;
import duck.ui.Ui;

/**
 * Displays guidance for every command that Duck understands.
 */
public class HelpCommand extends Command {
    /** Creates a help command. */
    public HelpCommand() {
    }

    /** Shows Duck's command guide. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
