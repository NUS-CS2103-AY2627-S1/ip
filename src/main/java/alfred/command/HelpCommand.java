package alfred.command;

import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Shows the compact list of commands Alfred understands.
 */
public class HelpCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showHelp();
    }
}
