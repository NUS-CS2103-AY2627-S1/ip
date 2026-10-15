package stephen.command;

import java.util.List;

import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Finds tasks whose descriptions contain a search phrase. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the given keyword.
     *
     * @param keyword search phrase to match
     */
    public FindCommand(String keyword) {
        assert keyword != null && !keyword.isBlank()
                : "A find command must contain a non-blank parsed keyword";
        this.keyword = keyword;
    }

    /**
     * Finds matching tasks and displays the results.
     *
     * @param tasks task list to search
     * @param ui console UI used to display the results
     * @param storage persistence service; not used by this command
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.find(keyword);
        ui.showFindResults(matches, keyword);
    }
}
