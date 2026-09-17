package notmarth.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import notmarth.exception.NotMarthException;

/** Stores contacts in their display order. */
public final class ContactList {
    private final ArrayList<Contact> contacts;

    /** Creates an empty contact list. */
    public ContactList() {
        this(List.of());
    }

    /**
     * Creates a contact list from saved contacts.
     *
     * @param loadedContacts the contacts recovered from storage.
     */
    public ContactList(List<Contact> loadedContacts) {
        if (loadedContacts == null) {
            throw new IllegalArgumentException("A contact list needs a contact collection.");
        }
        contacts = new ArrayList<>(loadedContacts);
    }

    /** Adds a contact to the end of the list. */
    public void add(Contact contact) throws NotMarthException {
        if (contact == null) {
            throw new IllegalArgumentException("A contact list cannot contain null.");
        }
        contacts.add(contact);
    }

    /**
     * Removes a contact by its one-based number.
     *
     * @param contactNumber the contact number to remove.
     * @return the removed contact.
     * @throws NotMarthException if the number is invalid.
     */
    public Contact delete(int contactNumber) throws NotMarthException {
        if (contacts.isEmpty()) {
            throw new NotMarthException("There are no contacts yet. Add a contact before deleting one.");
        }
        if (contactNumber < 1 || contactNumber > contacts.size()) {
            throw new NotMarthException("That contact number is not in your list. Use a number from 1 to "
                    + contacts.size() + ".");
        }
        return contacts.remove(contactNumber - 1);
    }

    /** @return the contact at a zero-based index */
    public Contact get(int index) {
        return contacts.get(index);
    }

    /** @return the number of contacts */
    public int size() {
        return contacts.size();
    }

    /** @return a read-only view of the contacts */
    public List<Contact> asList() {
        return Collections.unmodifiableList(contacts);
    }
}
