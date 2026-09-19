package gary.contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the contacts stored by the chatbot.
 */
public class ContactList {
    private final ArrayList<Contact> contacts;

    /**
     * Creates an empty contact list.
     */
    public ContactList() {
        contacts = new ArrayList<>();
    }

    /**
     * Creates a contact list containing the given contacts.
     *
     * @param contacts Initial contacts to store.
     */
    public ContactList(List<Contact> contacts) {
        assert contacts != null : "Initial contact list should not be null";
        assert contacts.stream().noneMatch(contact -> contact == null)
                : "Initial contact list should not contain null contacts";

        this.contacts = new ArrayList<>(contacts);
    }

    /**
     * Returns the number of stored contacts.
     *
     * @return Number of contacts.
     */
    public int size() {
        return contacts.size();
    }

    /**
     * Returns the contact at the given one-based position.
     *
     * @param contactNumber One-based contact number.
     * @return Contact at the requested position.
     */
    public Contact getContact(int contactNumber) {
        assert isValidContactNumber(contactNumber) : "Contact number should refer to an existing contact";

        return contacts.get(contactNumber - 1);
    }

    /**
     * Returns an immutable snapshot of all contacts.
     *
     * @return Snapshot of the stored contacts.
     */
    public List<Contact> getContacts() {
        return List.copyOf(contacts);
    }

    /**
     * Adds a contact to the end of the list.
     *
     * @param contact Contact to add.
     */
    public void add(Contact contact) {
        assert contact != null : "Contact to add should not be null";

        contacts.add(contact);
    }

    /**
     * Deletes and returns the contact at the given one-based position.
     *
     * @param contactNumber One-based contact number.
     * @return Deleted contact.
     */
    public Contact delete(int contactNumber) {
        assert isValidContactNumber(contactNumber) : "Contact number should refer to an existing contact";

        return contacts.remove(contactNumber - 1);
    }

    private boolean isValidContactNumber(int contactNumber) {
        return contactNumber >= 1 && contactNumber <= contacts.size();
    }
}
