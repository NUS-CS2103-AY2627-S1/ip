package dulio.command;

import java.io.IOException;

import dulio.task.Task;
import dulio.task.TaskList;
import dulio.ui.Ui;

/**
 * Deletes a task by its one-based list number.
 */
public class DeleteCommand extends Command {
    private int taskNumber;

    /**
     * Creates a delete command.
     *
     * @param taskNumber The one-based task number.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the task and reports the result.
     *
     * @param tasks The task list to modify.
     * @param ui The interface used for the response.
     * @throws IOException If persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws IOException {
        Task task = tasks.delete(taskNumber);
        if (task == null) {
            ui.showInvalidIndex();
        } else {
            ui.showDeleted(task, tasks.size());
        }
    }
}
