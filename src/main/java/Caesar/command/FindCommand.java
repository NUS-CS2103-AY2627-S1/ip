package caesar.command;

import caesar.exception.CaesarException;
import caesar.storage.Storage;
import caesar.task.TaskList;
import caesar.ui.Ui;

/** Finds tasks whose descriptions contain a supplied keyword. */
public class FindCommand extends Command {
    /** Keyword used to match task descriptions. */
    private final String keyword;

    /** Creates a find command for the supplied keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Finds tasks including key word. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CaesarException {
        TaskList foundTasks = tasks.find(keyword);
        ui.showTaskFound(foundTasks);
    }
}
