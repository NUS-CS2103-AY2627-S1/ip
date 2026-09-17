package panda.command;

import panda.task.TaskList;
import panda.ui.Ui;

/**
 * Displays every task stored during the current Panda session.
 */
public class ListTasksCommand implements Command {

    /**
     * Retrieves and displays the full formatted list of all current tasks.
     *
     * @param taskList Active task list.
     */
    @Override
    public void execute(TaskList taskList) {
        Ui.printMessage(taskList.toString());
    }
}
