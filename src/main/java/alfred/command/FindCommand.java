package alfred.command;

import alfred.Storage;
import alfred.Ui;
import alfred.task.TaskList;

/**
 * Shows the tasks whose description contains a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that will show the tasks matching one keyword.
     *
     * @param keyword the text to search descriptions for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks, keyword);
    }
}
