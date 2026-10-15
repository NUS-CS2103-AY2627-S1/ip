package ff15.command;

import java.io.IOException;

import ff15.FF15Exception;
import ff15.Storage;
import ff15.Ui;
import ff15.contact.Contact;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/** Removes a contact from the contact list. */
public class ContactDeleteCommand extends Command {
    private final int number;

    /**
     * Creates a command that will remove one contact from the list.
     *
     * @param number the contact number the user typed, counting from 1.
     */
    public ContactDeleteCommand(int number) {
        this.number = number;
    }

    /**
     * Removes the numbered contact, saves the list, and reports what was removed
     * along with how many contacts are left.
     *
     * @throws FF15Exception if the number does not name a contact in the list.
     * @throws IOException if the updated list could not be saved.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage)
            throws FF15Exception, IOException {
        Contact contact = contacts.delete(number);
        storage.saveContacts(contacts);
        ui.showContactRemoved(contact, contacts.size());
    }
}
