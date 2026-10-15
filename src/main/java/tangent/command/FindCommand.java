package tangent.command;

import tangent.storage.Storage;
import tangent.task.TaskList;
import tangent.ui.Ui;

/** Finds a task matching the specified keyword. */
public class FindCommand extends Command {
    /** The keyword to be matched on. */
    private final String keyword;

    /** Creates a command that finds tasks matching the keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Shows all tasks in {@code tasks} matching the {@code keyword}.*/
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        TaskList matchingTasks = tasks.find(keyword);
        ui.showMatchingTasks(keyword, matchingTasks);
    }
}
