package xiaozhi.command;

import java.util.ArrayList;

import xiaozhi.storage.Storage;
import xiaozhi.task.Task;
import xiaozhi.task.TaskList;
import xiaozhi.ui.Ui;

/**
 * Finds every task whose description contains a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a FindCommand that will search for the given keyword when executed.
     *
     * @param keyword The keyword to search task descriptions for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds every task in {@code tasks} whose description contains the
     * keyword, and reports the matches through {@code ui}. Does not touch storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage, CommandHistory history) {
        ArrayList<Task> matches = tasks.find(keyword);
        ui.showFound(matches);
    }
}
