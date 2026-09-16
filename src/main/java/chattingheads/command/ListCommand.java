package chattingheads.command;

import chattingheads.task.TaskList;
import chattingheads.ui.Ui;

/**
 * Represents a command that lists all tasks in the task list.
 */
public class ListCommand extends Command {

    @Override
    public String execute(TaskList taskList, Ui ui) {
        assert taskList != null : "TaskList should not be null";
        assert ui != null : "Ui should not be null";

        return ui.getTaskListMessage(taskList);
    }
}
