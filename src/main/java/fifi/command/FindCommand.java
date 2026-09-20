package fifi.command;

import fifi.Storage;
import fifi.TaskList;
import fifi.Ui;

/**
 * Shows tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the given keyword.
     *
     * @param keyword the keyword to search for
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        TaskList matchingTasks = tasks.findTasksByKeyword(keyword);
        ui.showResponse(String.format(
                "Here are the matching tasks in your list:%s",
                matchingTasks.toDisplayString()));
    }
}
