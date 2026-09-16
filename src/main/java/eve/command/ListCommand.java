package eve.command;

import eve.Storage;
import eve.Ui;
import eve.task.TaskList;

/** Shows every task currently in the list. */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
