package lebron.command;

import lebron.task.TaskList;
import lebron.ui.Ui;

/**
 * Represents the "list" command, which displays the current TaskList.
 */
public class ListCommand implements Command {
    @Override
    public String execute(TaskList taskList, Ui ui) {
        return ui.showTaskList(taskList);
    }
}
