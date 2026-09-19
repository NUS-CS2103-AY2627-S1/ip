package carl.commands;

import carl.task.TaskList;
import carl.task.TaskManager;
import carl.ui.Ui;

/**
 * Represents a command to list all current tasks to the user.
 */
public class ListCommand implements Command {


    /**
     * {@inheritDoc}
     * Displays all existing tasks to the user.
     *
     * @param ui      User interface for interacting with the user.
     * @param storage Task manager handling task data persistence.
     * @param tasks   List of current tasks.
     * @param raw     Raw input string entered by the user.
     * @return result containing the formatted task list.
     */
    @Override
    public CommandResult onRun(Ui ui, TaskManager storage, TaskList tasks, String raw) {
        return CommandResult.createSuccess(ui.showTaskList(tasks.getAllTasks()));
    }
}
