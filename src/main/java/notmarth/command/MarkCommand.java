package notmarth.command;

import notmarth.exception.NotMarthException;
import notmarth.model.ContactList;
import notmarth.model.Task;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes a command that marks a task as complete. */
public final class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a mark command for a one-based task number.
     *
     * @param taskNumber the task number to complete.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task complete, persists the change, and reports the result.
     *
     * @param tasks the current task list.
     * @param ui the console interaction handler.
     * @param storage the task archive handler.
     * @throws NotMarthException if the task number is invalid.
     */
    @Override
    public void execute(
            TaskList tasks, ContactList contacts, Ui ui, Storage storage) throws NotMarthException {
        Task task = tasks.mark(taskNumber);
        saveTasks(tasks, contacts, ui, storage);
        ui.showTaskMarked(task);
    }
}
