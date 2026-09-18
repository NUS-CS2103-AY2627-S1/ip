package benjamin.command;

import benjamin.storage.Storage;
import benjamin.task.TaskList;
import benjamin.ui.Ui;

/** Shows the tasks whose description contains a given keyword. */
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

    /** Shows the matching tasks. Nothing is changed, so nothing is saved. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
