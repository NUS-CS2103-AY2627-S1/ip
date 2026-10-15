package duncan.command;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.TaskList;

/** Shows every task currently in the task list. */
public class ListCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuncanException {
        ui.showTaskList(tasks.getTasks());
    }
}
