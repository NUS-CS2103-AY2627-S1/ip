package panda.command;

import panda.exception.ApplicationException;
import panda.storage.Storage;
import panda.task.Task;
import panda.task.TaskList;
import panda.ui.Ui;

/**
 * Removes one existing task from the task list.
 */
public class DeleteTaskCommand implements Command {
    private final int taskNumber;

    /**
     * Creates a command for the displayed task number.
     *
     * @param taskNumber One-based task number entered by the user.
     */
    public DeleteTaskCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Executes the task deletion from the task list and displays a confirmation message.
     *
     * @param taskList Active task list.
     * @throws ApplicationException If the task number is out of bounds.
     */
    @Override
    public void execute(TaskList taskList) throws ApplicationException {
        Task task = taskList.delete(taskNumber);
        Storage.saveState(taskList);
        int taskCount = taskList.getSize();
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        Ui.printMessage("Ah, the ancient technique of giving up. Deleted:"
                + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + "You still have " + taskCount + " " + taskNoun + " left to disappoint.");
    }
}
