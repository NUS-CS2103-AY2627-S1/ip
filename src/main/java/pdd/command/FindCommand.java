package pdd.command;

import pdd.storage.Storage;
import pdd.task.TaskList;
import pdd.ui.Ui;

/** Lists the tasks whose description contains a given keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a command that will list the tasks whose description contains the given keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.findMatching(keyword));
    }
}
