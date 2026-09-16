package downtowngurl.command;

import downtowngurl.storage.Storage;
import downtowngurl.task.TaskList;
import downtowngurl.ui.Ui;

/**
 * Represents the command that finds tasks containing a keyword in their descriptions.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command for finding tasks with descriptions that contain the keyword.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Storage storage, Ui ui) {
        ui.showMatchingTasks(tasks.findByKeyword(this.keyword));
    }
}
