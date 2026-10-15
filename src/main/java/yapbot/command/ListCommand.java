package yapbot.command;

import yapbot.task.TaskList;
import yapbot.ui.Ui;

/**
 * Represents the "list" command, which shows every task in the list.
 */
public class ListCommand extends Command {

    /**
     * Shows every task currently in the task list. Does not modify it.
     *
     * @param taskList the task list to show.
     * @param ui the Ui to show the list on.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) {
        ui.showTaskList(taskList.toArray(), taskList.size());
    }
}