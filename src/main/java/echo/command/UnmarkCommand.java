package echo.command;

import echo.EchoException;
import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Represents the command to mark a task as undone.
 */
public class UnmarkCommand extends TaskNumberCommand {
    /**
     * Creates an unmark command.
     *
     * @param taskNumber One-based task number.
     */
    public UnmarkCommand(int taskNumber) {
        super(Type.UNMARK, taskNumber);
    }

    /**
     * Unmarks the selected task and displays the result.
     */
    @Override
    public String execute(TaskManager taskManager, Ui ui) throws EchoException {
        return ui.showUnmarked(taskManager.unmarkTask(getTaskNumber()));
    }
}
