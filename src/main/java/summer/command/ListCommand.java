package summer.command;

import summer.storage.Storage;
import summer.task.TaskList;
import summer.ui.Ui;

/** The "list" command: shows every task currently stored. */
public class ListCommand extends Command {
    /** Prints every stored task as a numbered list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage(tasks.list());
    }
}
