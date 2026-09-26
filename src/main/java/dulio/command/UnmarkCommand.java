package dulio.command;

import java.io.IOException;

import dulio.task.Task;
import dulio.task.TaskList;
import dulio.ui.Ui;

/**
 * Marks a task as not completed.
 */
public class UnmarkCommand extends Command {
    private int taskNumber;

    /**
     * Creates an unmark command.
     *
     * @param taskNumber The one-based task number.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task as incomplete and reports the result.
     *
     * @param tasks The task list to modify.
     * @param ui The interface used for the response.
     * @throws IOException If persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws IOException {
        Task task = tasks.unmark(taskNumber);
        if (task == null) {
            ui.showInvalidIndex();
        } else {
            ui.showMarked(task, false);
        }
    }
}
