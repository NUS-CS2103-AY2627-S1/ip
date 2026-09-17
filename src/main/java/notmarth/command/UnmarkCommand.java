package notmarth.command;

import notmarth.exception.NotMarthException;
import notmarth.model.ContactList;
import notmarth.model.Task;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes a command that marks a task as incomplete. */
public final class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates an unmark command for a one-based task number.
     *
     * @param taskNumber the task number to reopen.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Reopens the selected task, persists the change, and reports the result.
     *
     * @param tasks the current task list.
     * @param ui the console interaction handler.
     * @param storage the task archive handler.
     * @throws NotMarthException if the task number is invalid.
     */
    @Override
    public void execute(
            TaskList tasks, ContactList contacts, Ui ui, Storage storage) throws NotMarthException {
        Task task = tasks.unmark(taskNumber);
        saveTasks(tasks, contacts, ui, storage);
        ui.showTaskUnmarked(task);
    }
}
