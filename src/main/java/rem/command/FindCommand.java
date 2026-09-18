package rem.command;

import java.util.List;

import rem.storage.Storage;
import rem.task.Task;
import rem.task.TaskList;
import rem.ui.Ui;

/**
 * Displays tasks whose descriptions or notes contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays tasks with descriptions or notes matching this command's keyword.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the results.
     * @param storage Storage available to commands that need persistence.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findTasks(keyword);
        if (matchingTasks.isEmpty()) {
            ui.showMessage("Didn't find any tasks matching '" + keyword + "'. Try another word?");
            return;
        }
        ui.showMessage("Found these!");
        ui.showNumberedTasks(matchingTasks);
    }
}
