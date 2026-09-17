package panda.command;

import panda.exception.ApplicationException;
import panda.storage.Storage;
import panda.task.Task;
import panda.task.TaskList;
import panda.ui.Ui;

/**
 * Removes the mark from one existing task.
 */
public class UnmarkTaskCommand implements Command {
    private final int taskNumber;

    /**
     * Creates a command for the displayed task number.
     *
     * @param taskNumber One-based task number entered by the user.
     */
    public UnmarkTaskCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Unmarks the designated task (marks as not done) and displays a confirmation message.
     *
     * @param taskList Active task list.
     * @throws ApplicationException If the task number is out of bounds or already unmarked.
     */
    @Override
    public void execute(TaskList taskList) throws ApplicationException {
        Task task = taskList.unmarkTask(taskNumber);
        Storage.saveState(taskList);
        Ui.printMessage("And there it is—the backslide. This task is back:"
                + System.lineSeparator() + "  " + task);
    }
}
