package milo.command;

import milo.storage.Storage;
import milo.task.TaskList;
import milo.ui.Ui;

/** Displays the available commands without modifying saved data. */
public class HelpCommand extends Command {
    /** Creates a command that displays help information. */
    public HelpCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
