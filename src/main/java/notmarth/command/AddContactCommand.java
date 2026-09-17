package notmarth.command;

import java.io.IOException;

import notmarth.exception.NotMarthException;
import notmarth.model.Contact;
import notmarth.model.ContactList;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Adds and saves a contact. */
public final class AddContactCommand extends Command {
    private final Contact contact;

    /** Creates a command for the supplied contact. */
    public AddContactCommand(Contact contact) {
        this.contact = contact;
    }

    @Override
    public void execute(
            TaskList tasks, ContactList contacts, Ui ui, Storage storage) throws NotMarthException {
        contacts.add(contact);
        saveState(tasks, contacts, ui, storage);
        ui.showContactAdded(contact, contacts.size());
    }

    private void saveState(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        try {
            storage.save(tasks.asList(), contacts.asList());
        } catch (IOException exception) {
            ui.showError("I couldn't save the battle plan to disk. Your current session is still active.");
        }
    }
}
