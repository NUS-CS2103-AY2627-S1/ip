package thomas.command;

import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/** Displays the commands supported by Thomas. */
public class HelpCommand extends Command {
    /** Displays task and place command usage. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
