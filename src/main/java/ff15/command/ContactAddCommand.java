package ff15.command;

import java.io.IOException;
import java.util.OptionalInt;

import ff15.FF15Exception;
import ff15.Storage;
import ff15.Ui;
import ff15.contact.Contact;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/** Adds a contact to the contact list. */
public class ContactAddCommand extends Command {
    private final Contact contact;

    /**
     * Creates a command that will add one contact.
     *
     * @param contact the contact the parser built from the user input.
     */
    public ContactAddCommand(Contact contact) {
        this.contact = contact;
    }

    /**
     * Adds the contact, saves the list, and reports what was added along with how
     * many contacts there are now. A contact the list already has, identical in
     * every field, is refused, naming the one it repeats, as an add of a task is.
     *
     * @throws FF15Exception if the list already holds this contact.
     * @throws IOException if the updated list could not be saved.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage)
            throws FF15Exception, IOException {
        OptionalInt existing = contacts.findSame(contact);
        if (existing.isPresent()) {
            throw new FF15Exception("I already know them. That's contact " + existing.getAsInt()
                    + ". I know everyone, remember?");
        }
        contacts.add(contact);
        storage.saveContacts(contacts);
        ui.showContactAdded(contact, contacts.size());
    }
}
