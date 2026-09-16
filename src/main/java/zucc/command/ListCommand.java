package zucc.command;

import zucc.ZuccException;
import zucc.storage.Storage;
import zucc.task.TaskList;
import zucc.ui.Ui;

/**
 * Displays all tasks in their current order.
 */
public final class ListCommand extends Command {
    /** Creates a list command awaiting values from Parser. */
    ListCommand() {
        super("list");
    }

    /**
     * Shows the numbered task list without modifying it.
     *
     * @param tasks task collection to display.
     * @param ui user interface through which the list is shown.
     * @param storage persistent storage; unchanged by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZuccException {
        requireNoArgument();
        ui.showMessage("Here are the tasks in your list:\n" + tasks);
    }
}
