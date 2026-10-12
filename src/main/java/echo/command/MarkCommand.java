package echo.command;

import echo.EchoException;
import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Represents the command to mark a task as completed.
 */
public class MarkCommand extends TaskNumberCommand {
    /**
     * Creates a mark command.
     *
     * @param taskNumber One-based task number.
     */
    public MarkCommand(int taskNumber) {
        super(Type.MARK, taskNumber);
    }

    /**
     * Marks the selected task and displays the result.
     */
    @Override
    public String execute(TaskManager taskManager, Ui ui) throws EchoException {
        return ui.showMarked(taskManager.markTask(getTaskNumber()));
    }
}
