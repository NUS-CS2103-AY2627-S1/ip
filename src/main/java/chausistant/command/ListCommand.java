package chausistant.command;

import chausistant.storage.Storage;
import chausistant.task.Task;
import chausistant.task.TaskList;
import chausistant.ui.Ui;

/** Command that displays every task currently in the task list. */
public class ListCommand extends Command {

    /** Displays all tasks in their current order. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks().stream().map(Task::printTask).toList());
    }
}
