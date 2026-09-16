package puyo.command;

import puyo.PuyoException;
import puyo.storage.Storage;
import puyo.task.TaskList;
import puyo.ui.Ui;

/**
 * Represents a command to display all tasks currently in the task list.
 */
public class ListCommand extends Command {

    /**
     * Executes the command by displaying the current list of tasks to the user.
     *
     * @param tasks The list of tasks.
     * @param ui The user interface for interaction.
     * @param storage The storage handler.
     * @throws PuyoException If an error occurs during execution.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PuyoException {
        ui.showTaskList(tasks);
    }
}
