package zhangwei.command;

import zhangwei.storage.Storage;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/** Shows every task currently in the list. */
public class ListCommand extends Command {

    /** Creates the command that shows the task list. */
    public ListCommand() {
    }

    /**
     * Prints every task in the list, numbered as the user refers to them.
     *
     * @param tasks the task list to show.
     * @param ui used to print the list.
     * @param storage unused, because listing changes nothing that needs saving.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
