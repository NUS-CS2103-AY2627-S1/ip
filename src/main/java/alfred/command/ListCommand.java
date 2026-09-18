package alfred.command;

import alfred.Storage;
import alfred.Ui;
import alfred.task.TaskList;

/**
 * Shows every stored task, in the order they are stored.
 */
public class ListCommand extends Command {
    /** Creates a command that will show the whole list. */
    public ListCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showList(tasks);
    }
}
