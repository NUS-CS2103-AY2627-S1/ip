package notmarth.command;

import notmarth.exception.NotMarthException;
import notmarth.model.ContactList;
import notmarth.model.Task;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes a command that removes a task from the battle plan. */
public final class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a delete command for a one-based task number.
     *
     * @param taskNumber the task number to remove.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the selected task, persists the updated battle plan, and reports the result.
     *
     * @param tasks the current task list.
     * @param ui the console interaction handler.
     * @param storage the task archive handler.
     * @throws NotMarthException if the task number is invalid.
     */
    @Override
    public void execute(
            TaskList tasks, ContactList contacts, Ui ui, Storage storage) throws NotMarthException {
        Task deletedTask = tasks.delete(taskNumber);
        saveTasks(tasks, contacts, ui, storage);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
