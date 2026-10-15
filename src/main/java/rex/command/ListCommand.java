package rex.command;

import rex.TaskList;
import rex.Ui;

/** Shows every task in the list. */
public class ListCommand extends Command {
    /** Shows every task, numbered from 1. */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTaskList(tasks);
    }
}
