package lebronjames.command;

import lebronjames.storage.Storage;
import lebronjames.task.TaskList;
import lebronjames.ui.Ui;

/**
 * Shows the tasks whose description contains a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for the given keyword.
     *
     * @param keyword Text to look for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.findTasksWithKeyword(keyword));
    }
}
