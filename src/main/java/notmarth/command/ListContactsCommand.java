package notmarth.command;

import notmarth.model.ContactList;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Displays all saved contacts. */
public final class ListContactsCommand extends Command {
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        ui.showContacts(contacts);
    }
}
