package zhangwei.command;

import java.util.List;

import zhangwei.storage.Storage;
import zhangwei.task.Task;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/**
 * Shows the tasks whose description contains a given keyword.
 *
 * <p>Once a list grows past a screenful, reading it to find one task is the
 * slow part. Searching is a query rather than a change, so like
 * {@link ListCommand} this command saves nothing.
 */
public class FindCommand extends Command {

    private final String keyword;

    /**
     * Creates a command that will show the tasks matching the given keyword.
     *
     * @param keyword the word to look for in each task's description.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Shows every task whose description contains the keyword, numbered from 1
     * among the matches rather than by their position in the full list.
     *
     * @param tasks the task list to search.
     * @param ui used to show the matching tasks.
     * @param storage unused, because searching changes nothing that needs saving.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.find(keyword);
        ui.showMatchingTasks(matches);
    }
}
