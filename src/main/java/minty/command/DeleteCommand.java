package minty.command;

import minty.task.Task;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Deletes one task from Minty's task list.
 */
public class DeleteCommand extends IndexedTaskCommand {

    /**
     * Creates a delete command from the user's complete input.
     *
     * @param fullCommand complete delete command.
     */
    public DeleteCommand(String fullCommand) {
        super(fullCommand, CommandType.DELETE);
    }

    /**
     * Deletes the selected task.
     *
     * @param tasks task list to update.
     * @param taskIndex validated zero-based task index.
     * @return deleted task.
     */
    @Override
    protected Task updateTask(TaskList tasks, int taskIndex) {
        return tasks.delete(taskIndex);
    }

    /**
     * Returns Minty's deletion confirmation.
     *
     * @return deletion confirmation message.
     */
    @Override
    protected String getConfirmationMessage() {
        return "Making room! I've removed:";
    }

    /**
     * Shows the number of tasks remaining after deletion.
     *
     * @param tasks updated task list.
     * @param ui command-line interface used for the response.
     */
    @Override
    protected void showAdditionalResponse(TaskList tasks, Ui ui) {
        ui.showTaskCount(tasks.size());
    }
}
