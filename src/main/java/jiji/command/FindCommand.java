package jiji.command;

import java.util.List;

import jiji.storage.Storage;
import jiji.task.Task;
import jiji.task.TaskList;
import jiji.ui.Ui;

/**
 * Command that finds tasks matching a specified search keyword.
 */
public class FindCommand extends Command {

    private final String keyword;

    /**
     * Constructs a FindCommand with the specified search keyword.
     *
     * @param keyword The keyword string to search for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Executes the find command by filtering tasks and displaying matches via the UI.
     *
     * @param tasks The task list searched.
     * @param ui The UI handler for displaying output.
     * @param storage The storage handler.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findTasks(keyword);
        ui.showMatchingTasks(matchingTasks);
        return ui.formatMatchingTasks(matchingTasks);
    }
}
