package pdd.command;

import pdd.storage.Storage;
import pdd.task.TaskList;
import pdd.ui.Ui;

/** Prints every task currently in the list. */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
    }
}
