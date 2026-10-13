package ruby.command;

import ruby.RubyException;
import ruby.contact.Contact;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;

/**
 * Adds a contact to Ruby's contact list.
 */
public class AddContactCommand extends Command {
    private final String name;
    private final String phoneNumber;
    private final String email;
    private final String address;

    /**
     * Creates a command that adds a contact with the given details.
     *
     * @param name        Name of the contact.
     * @param phoneNumber Phone number of the contact, or an empty string.
     * @param email       Email of the contact, or an empty string.
     * @param address     Address of the contact, or an empty string.
     */
    public AddContactCommand(String name, String phoneNumber, String email, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    @Override
    public String execute(TaskList tasks, ContactList contacts, Storage storage) throws RubyException {
        String response = contacts.addContact(new Contact(name, phoneNumber, email, address));
        storage.save(tasks, contacts);
        return response;
    }
}
