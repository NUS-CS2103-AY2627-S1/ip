package yuki.command;

import yuki.storage.Storage;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Displays every task in Yuki's task list.
 */
public class ListCommand extends Command {
    /** Displays the tasks without changing or saving them. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
    }
}
