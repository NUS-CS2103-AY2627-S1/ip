package et.command;

import et.storage.Storage;
import et.task.TaskList;
import et.ui.Ui;

/**
 * Displays tasks whose descriptions contain a given keyword.
 */
public class FindCommand extends Command {
    /** The text used to search task descriptions. */
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the supplied text.
     *
     * @param keyword the text to find in task descriptions
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
