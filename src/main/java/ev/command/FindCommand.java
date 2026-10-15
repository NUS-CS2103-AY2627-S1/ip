package ev.command;

import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.Ui;

/**
 * Shows the tasks whose description contains a keyword.
 */
public class FindCommand extends Command {

    private final String keyword;

    /**
     * Creates a command that will search for the given text.
     *
     * @param keyword the text the user is looking for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(keyword, tasks);
    }
}
