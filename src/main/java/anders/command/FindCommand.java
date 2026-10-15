package anders.command;

import anders.collection.TaskList;
import anders.storage.Storage;
import anders.ui.Ui;

/** Displays tasks whose descriptions contain a requested keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a find command for the supplied keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks, keyword);
    }
}
