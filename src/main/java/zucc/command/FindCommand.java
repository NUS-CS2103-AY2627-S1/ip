package zucc.command;

import zucc.ZuccException;
import zucc.storage.Storage;
import zucc.task.TaskList;
import zucc.ui.Ui;

/**
 * Displays tasks whose descriptions contain a supplied keyword.
 */
public final class FindCommand extends Command {
    /** Creates a find command awaiting values from Parser. */
    FindCommand() {
        super("find");
    }

    /**
     * Finds and displays tasks containing this command's keyword.
     *
     * @param tasks task collection to search.
     * @param ui user interface through which the results are shown.
     * @param storage persistent storage; unchanged by this command.
     * @throws ZuccException if the search keyword is missing.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZuccException {
        String keyword = requireArgument("a keyword");
        ui.showMessage("Here are the matching tasks in your list:\n"
                + tasks.formatTasksContaining(keyword));
    }
}
