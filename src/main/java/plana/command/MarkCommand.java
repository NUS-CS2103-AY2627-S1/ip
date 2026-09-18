package plana.command;

import plana.exception.PlanaException;
import plana.storage.Storage;
import plana.task.Task;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Marks a selected task as done and persists the updated task list.
 */
public class MarkCommand extends Command {
    private final String taskNumber;

    /**
     * Creates a mark command for a user-facing task number.
     *
     * @param taskNumber the one-based task number entered by the user.
     */
    public MarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task, saves the list, and displays the result.
     *
     * @param tasks the task list to update.
     * @param ui the user interface used for the response.
     * @param storage the storage collaborator used for persistence.
     * @throws PlanaException if the task number is invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PlanaException {
        boolean wasDone = tasks.isDone(TaskAction.MARK, taskNumber);
        Task markedTask = tasks.mark(taskNumber);
        if (!storage.saveTasks(tasks)) {
            restoreCompletionStatus(markedTask, wasDone);
            throw new PlanaException("Oops, I couldn't save that change."
                    + " Please check that the data folder is writable.");
        }
        ui.showTaskMarkedDone(markedTask);
    }

    private void restoreCompletionStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }
}
