package pulbot.command;

import pulbot.storage.Storage;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

/** Finds tasks whose descriptions contain a supplied keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a search command for the supplied keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Displays tasks whose descriptions contain the keyword. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks, keyword);
    }
}
