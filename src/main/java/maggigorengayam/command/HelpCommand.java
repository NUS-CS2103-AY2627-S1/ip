package maggigorengayam.command;

import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The {@code help} command: shows the list of supported commands and their syntax. */
public class HelpCommand extends Command {
    /** Returns a message listing every supported command and its syntax. */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.showHelp();
    }
}
