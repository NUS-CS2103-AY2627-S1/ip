package notmarth.command;

import java.io.IOException;

import notmarth.exception.NotMarthException;
import notmarth.model.Contact;
import notmarth.model.ContactList;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Deletes and saves a contact. */
public final class DeleteContactCommand extends Command {
    private final int contactNumber;

    /** Creates a command for a one-based contact number. */
    public DeleteContactCommand(int contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public void execute(
            TaskList tasks, ContactList contacts, Ui ui, Storage storage) throws NotMarthException {
        Contact deletedContact = contacts.delete(contactNumber);
        try {
            storage.save(tasks.asList(), contacts.asList());
        } catch (IOException exception) {
            ui.showError("I couldn't save the battle plan to disk. Your current session is still active.");
        }
        ui.showContactDeleted(deletedContact, contacts.size());
    }
}
