package echo.command;

import echo.EchoException;
import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Represents the command to delete a task based on index.
 */
public class DeleteCommand extends TaskNumberCommand {
    /**
     * Creates a delete command.
     *
     * @param taskNumber One-based task number.
     */
    public DeleteCommand(int taskNumber) {
        super(Type.DELETE, taskNumber);
    }

    /**
     * Deletes the selected task and displays the result. *
     */
    @Override
    public String execute(TaskManager taskManager, Ui ui) throws EchoException {
        int taskNumber = getTaskNumber();
        return ui.showDeleted(taskNumber, taskManager.deleteTask(taskNumber), taskManager.size());
    }
}
