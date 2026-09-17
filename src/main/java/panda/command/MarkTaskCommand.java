package panda.command;

import panda.exception.ApplicationException;
import panda.storage.Storage;
import panda.task.Task;
import panda.task.TaskList;
import panda.ui.Ui;

/**
 * Marks one existing task.
 */
public class MarkTaskCommand implements Command {
    private final int taskNumber;

    /**
     * Creates a command for the displayed task number.
     *
     * @param taskNumber One-based task number entered by the user.
     */
    public MarkTaskCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the designated task as completed and displays a confirmation message.
     *
     * @param taskList Active task list.
     * @throws ApplicationException If the task number is out of bounds or already marked.
     */
    @Override
    public void execute(TaskList taskList) throws ApplicationException {
        Task task = taskList.markTask(taskNumber);
        Storage.saveState(taskList);
        Ui.printMessage("You actually finished something? Screenshot this historic moment:"
                + System.lineSeparator() + "  " + task);
    }
}
