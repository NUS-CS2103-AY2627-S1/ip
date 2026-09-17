package notmarth.command;

import notmarth.exception.NotMarthException;
import notmarth.model.ContactList;
import notmarth.model.Task;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes a command that adds a parsed task to the battle plan. */
public final class AddCommand extends Command {
    private final Task task;

    /**
     * Creates an add command for a parsed task.
     *
     * @param task the task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, persists the updated battle plan, and reports the result.
     *
     * @param tasks the current task list.
     * @param ui the console interaction handler.
     * @param storage the task archive handler.
     * @throws NotMarthException if the task list cannot accept another task.
     */
    @Override
    public void execute(
            TaskList tasks, ContactList contacts, Ui ui, Storage storage) throws NotMarthException {
        tasks.add(task);
        saveTasks(tasks, contacts, ui, storage);
        ui.showTaskAdded(task, tasks.size());
    }
}
