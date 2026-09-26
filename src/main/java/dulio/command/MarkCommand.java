package dulio.command;

import java.io.IOException;

import dulio.task.Task;
import dulio.task.TaskList;
import dulio.ui.Ui;

/**
 * Marks a task as completed.
 */
public class MarkCommand extends Command {
    private int taskNumber;

    /**
     * Creates a mark command.
     *
     * @param taskNumber The one-based task number.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task as completed and reports the result.
     *
     * @param tasks The task list to modify.
     * @param ui The interface used for the response.
     * @throws IOException If persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws IOException {
        Task task = tasks.mark(taskNumber);
        if (task == null) {
            ui.showInvalidIndex();
        } else {
            ui.showMarked(task, true);
        }
    }
}
