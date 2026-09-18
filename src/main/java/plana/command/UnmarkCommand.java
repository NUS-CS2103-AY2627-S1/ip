package plana.command;

import plana.exception.PlanaException;
import plana.storage.Storage;
import plana.task.Task;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Marks a selected task as not done and persists the updated task list.
 */
public class UnmarkCommand extends Command {
    private final String taskNumber;

    /**
     * Creates an unmark command for a user-facing task number.
     *
     * @param taskNumber the one-based task number entered by the user.
     */
    public UnmarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task as not done, saves the list, and displays the result.
     *
     * @param tasks the task list to update.
     * @param ui the user interface used for the response.
     * @param storage the storage collaborator used for persistence.
     * @throws PlanaException if the task number is invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PlanaException {
        boolean wasDone = tasks.isDone(TaskAction.UNMARK, taskNumber);
        Task unmarkedTask = tasks.unmark(taskNumber);
        if (!storage.saveTasks(tasks)) {
            restoreCompletionStatus(unmarkedTask, wasDone);
            throw new PlanaException("Oops, I couldn't save that change."
                    + " Please check that the data folder is writable.");
        }
        ui.showTaskMarkedNotDone(unmarkedTask);
    }

    private void restoreCompletionStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }
}
