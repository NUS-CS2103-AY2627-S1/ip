package yachiyo.command;

import java.util.List;

import yachiyo.storage.Storage;
import yachiyo.task.NumberedTask;
import yachiyo.task.TaskList;
import yachiyo.ui.Ui;

/**
 * Displays tasks whose descriptions contain a specified keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the supplied keyword.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays matching tasks while retaining their numbers from the complete list.
     *
     * @param tasks Task list to search.
     * @param ui User interface used to display the results.
     * @param storage Unused because searching does not change stored data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<NumberedTask> matchingTasks = tasks.findTasks(keyword);

        if (matchingTasks.isEmpty()) {
            ui.showNoMatchingTasks(keyword);
            return;
        }

        ui.showMatchingTasksHeader(keyword);
        ui.showNumberedTasks(matchingTasks);
    }
}
