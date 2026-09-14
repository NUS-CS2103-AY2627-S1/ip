package nova.command;

import nova.Storage;
import nova.TaskList;
import nova.Ui;

/** Shows the tasks whose description contains a given keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that will search for the given keyword.
     *
     * @param keyword the text to look for in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(keyword, tasks.find(keyword));
    }
}
