package daddy.command;

import daddy.storage.Storage;
import daddy.task.TaskList;
import daddy.ui.Ui;

/**
 * Displays tasks whose descriptions contain a supplied keyword.
 */
public class FindCommand extends Command {
    /** Stores the keyword used to match task descriptions. */
    private final String keyword;

    /**
     * Creates a command that finds tasks containing a keyword.
     *
     * @param keyword the text to find in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Displays all tasks whose descriptions contain this command's keyword. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.getTasksMatching(keyword));
    }
}
