package kong.command;

import kong.storage.Storage;
import kong.task.TaskList;
import kong.ui.Ui;

/**
 * Shows tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for the specified keyword.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.findTasks(keyword));
    }
}
