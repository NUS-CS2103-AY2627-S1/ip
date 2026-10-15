package bob.command;

import bob.Storage;
import bob.Ui;
import bob.task.TaskList;

/**
 * Represents a request to find tasks containing a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the given keyword.
     *
     * @param keyword text to find in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showFoundTasks(tasks.find(keyword), tasks.asList());
    }
}
