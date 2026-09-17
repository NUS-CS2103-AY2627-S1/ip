package harvey.command;

import harvey.HarveyException;
import harvey.storage.Storage;
import harvey.task.TaskList;
import harvey.ui.Ui;
/**
 * Shows every task currently stored.
 * The only command that changes nothing, so it is also the only one that does not save.
 */
public class ListCommand extends Command {
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws HarveyException {
        if (tasks.isEmpty()) {
            throw new HarveyException("Your docket is empty. Bring me something, like: "
                    + CommandType.TODO.getExample());
        }
        return ui.formatTaskList(tasks);
    }
}
