package eve.command;

import eve.Storage;
import eve.Ui;
import eve.task.TaskList;

/** Shows the full list of available commands. */
public class HelpCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
