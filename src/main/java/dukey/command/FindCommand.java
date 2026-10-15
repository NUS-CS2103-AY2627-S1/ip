package dukey.command;

import dukey.storage.Storage;
import dukey.task.TaskList;
import dukey.ui.Ui;

/**
 * Shows tasks whose descriptions contain the search keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.find(this.keyword));
    }

    @Override
    public String getResponseStyleClass() {
        return STYLE_SEARCH;
    }
}
