package toothless.command;

import java.util.List;

import toothless.storage.Storage;
import toothless.task.Task;
import toothless.task.TaskList;
import toothless.ui.Ui;

/**
 * Coordinates finding tasks whose descriptions contain a search keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command for a validated keyword.
     *
     * @param keyword keyword obtained from the parser
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds and displays matching tasks without changing or saving the task list.
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        List<Task> matchingTasks = taskList.findTasks(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
