package duncan.command;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.TaskList;

/** Shows every task whose description contains a given keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword Text to search task descriptions for.
     */
    public FindCommand(String keyword) {
        assert keyword != null && !keyword.isEmpty();
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuncanException {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
