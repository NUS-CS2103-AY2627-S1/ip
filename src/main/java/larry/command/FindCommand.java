package larry.command;

import java.util.List;

import larry.storage.Storage;
import larry.task.Task;
import larry.task.TaskList;
import larry.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the keyword.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findByDescription(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
