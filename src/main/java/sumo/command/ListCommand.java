package sumo.command;

import java.io.IOException;

import sumo.storage.Storage;
import sumo.task.TaskList;
import sumo.ui.Ui;

/** Displays every task in the task list. */
public class ListCommand extends Command {
    /** Creates a command that displays the task list. */
    public ListCommand() {
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        ui.showTaskList(tasks.getTasks());
    }
}
