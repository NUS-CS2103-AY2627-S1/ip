package nova.command;

import nova.Storage;
import nova.TaskList;
import nova.Ui;

/** Shows every task in the list. */
public class ListCommand extends Command {

    /** Creates a command that shows the whole list. */
    public ListCommand() {
    }
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
