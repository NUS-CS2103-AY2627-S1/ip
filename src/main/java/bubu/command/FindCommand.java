package bubu.command;

import java.util.List;

import bubu.storage.Storage;
import bubu.task.Task;
import bubu.task.TaskList;
import bubu.ui.Ui;

/**
 * Represents a command to find tasks in the task list that match a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Constructs a FindCommand with the specified search keyword.
     *
     * @param keyword The substring to look for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findMatchingTasks(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
