package bobby;

import java.util.List;

/** Finds tasks whose descriptions contain a user-provided keyword. */
public class FindCommand extends Command {
    /** Keyword to search for in task descriptions. */
    private final String keyword;

    /**
     * Creates a find command for the given keyword.
     *
     * @param keyword text to look for in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Searches the task list and displays the matching tasks.
     *
     * @param tasks current task list to search
     * @param ui interface used to display the results
     * @param storage unused because searching does not change persisted data
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findByDescription(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
