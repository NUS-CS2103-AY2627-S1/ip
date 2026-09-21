package maggigorengayam.command;

import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The {@code list} command: shows every task currently in the list. */
public class ListCommand extends Command {
    /** Returns a message listing every task currently in {@code tasks}. */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.showTaskList(tasks);
    }
}
