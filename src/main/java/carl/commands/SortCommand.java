package carl.commands;

import carl.exceptions.CarlException;
import carl.task.TaskList;
import carl.task.TaskManager;
import carl.ui.Ui;

/**
 * Displays tasks sorted alphabetically without changing their saved order.
 */
public class SortCommand implements Command {
    @Override
    public CommandResult onRun(Ui ui, TaskManager storage, TaskList tasks, String raw) throws CarlException {
        return CommandResult.createSuccess(ui.showTaskList(tasks.getSortedTasks()));
    }
}
