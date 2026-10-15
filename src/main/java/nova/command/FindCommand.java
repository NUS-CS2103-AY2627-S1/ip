package nova.command;

import java.util.List;

import nova.task.TaskList;
import nova.task.TaskList.NumberedTask;
import nova.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that finds tasks containing the given keyword.
     *
     * @param keyword keyword to search for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds tasks containing the keyword and displays the matches.
     *
     * @param tasks task list to search.
     * @param ui UI used to display the matching tasks.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        List<NumberedTask> matchingTasks = tasks.findTasks(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
