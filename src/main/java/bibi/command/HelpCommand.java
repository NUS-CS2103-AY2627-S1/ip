package bibi.command;

import bibi.Storage;
import bibi.Ui;
import bibi.task.TaskList;

/**
 * Lists the commands Bibi understands.
 */
public class HelpCommand extends Command {
    /**
     * Creates a command that will show the list of supported commands.
     */
    public HelpCommand() {
    }


    /**
     * Shows every command Bibi understands.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
