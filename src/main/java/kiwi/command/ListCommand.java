package kiwi.command;

import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.TaskList;

/**
 * Lists all tasks in the task list.
 */
public class ListCommand extends Command {

    /**
     * Prints every task in the list.
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
