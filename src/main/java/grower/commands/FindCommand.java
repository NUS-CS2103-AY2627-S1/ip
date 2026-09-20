package grower.commands;

import java.util.List;

import grower.tasks.Task;
import grower.tasks.TaskList;
import grower.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions.
     *
     * @param keyword Text to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean execute(TaskList tasks, Ui ui) {
        List<Task> matches = tasks.findTasks(keyword);
        ui.showSearchResults(matches);
        return true;
    }
}
