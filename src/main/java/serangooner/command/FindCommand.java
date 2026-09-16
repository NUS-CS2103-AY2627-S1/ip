package serangooner.command;

import serangooner.storage.Storage;
import serangooner.task.TaskList;
import serangooner.ui.Ui;

/**
 * Shows the tasks whose description carries a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Constructs a command that searches for the given keyword.
     *
     * @param keyword Text to look for in each task description.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.formatMatchingTasks(tasks.getEntriesMatching(keyword), keyword);
    }
}
