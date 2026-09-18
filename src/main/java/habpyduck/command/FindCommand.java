package habpyduck.command;

import java.util.ArrayList;

import habpyduck.storage.Storage;
import habpyduck.task.Task;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for matching task descriptions.
     *
     * @param keyword the keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds matching tasks and shows them to the user.
     *
     * @param tasks the task list to search.
     * @param ui the UI used to show command results.
     * @param storage the storage used by other commands.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ArrayList<Task> matchingTasks = tasks.findByKeyword(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
