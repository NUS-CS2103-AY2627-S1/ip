package lumine.command;

import lumine.task.Task;
import lumine.task.TaskList;
import lumine.ui.Ui;

/**
 * Deletes a task by its displayed number.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that will delete the task at the given 1-based position.
     *
     * @param taskNumber 1-based index of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * {@inheritDoc}
     * Removes the task and prints a confirmation with the updated list size.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) {
        Task task = taskList.deleteTask(taskNumber);
        ui.showMessages(
                "Noted. I've removed this task:",
                task.toString(),
                "Now you have " + taskList.size() + " tasks in the list.");
    }
}
