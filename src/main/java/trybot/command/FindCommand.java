package trybot.command;

import trybot.storage.Storage;
import trybot.task.TaskList;
import trybot.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command.
     *
     * @param keyword keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays tasks whose descriptions contain the search keyword.
     *
     * @param tasks current task list.
     * @param ui user-interface handler.
     * @param storage task persistence handler, unused because finding is read-only.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.findByDescription(keyword));
    }

    /**
     * Indicates that finding tasks does not end the TryBot session.
     *
     * @return always false
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
