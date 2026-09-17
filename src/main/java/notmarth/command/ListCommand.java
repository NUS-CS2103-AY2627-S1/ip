package notmarth.command;

import notmarth.model.ContactList;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes a command that displays every task in the battle plan. */
public final class ListCommand extends Command {
    /** Creates a list command. */
    public ListCommand() {
    }

    /**
     * Displays every task in its current battle-plan order.
     *
     * @param tasks the task list to display.
     * @param ui the console interaction handler.
     * @param storage the task archive handler, which is unused.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        ui.showTasks(tasks);
    }
}
