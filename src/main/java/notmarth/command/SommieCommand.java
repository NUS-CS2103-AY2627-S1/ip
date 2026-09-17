package notmarth.command;

import notmarth.model.ContactList;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes the hidden Sommie Easter egg command. */
public final class SommieCommand extends Command {
    /** Creates the hidden Sommie command. */
    public SommieCommand() {
    }

    /**
     * Displays the hidden Sommie Easter egg without changing the battle plan.
     *
     * @param tasks the current task list, which is unchanged.
     * @param ui the console interaction handler.
     * @param storage the task archive handler, which is unused.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        ui.showSommieMessage();
    }
}
