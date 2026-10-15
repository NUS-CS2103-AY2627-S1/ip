package laby.contact;

import java.util.List;
import java.util.stream.Collectors;

import laby.LabyException;

/** Stores and provides operations for the application's contacts. */
public class ContactList {
    private final List<Contact> contacts;

    /**
     * Creates a contact list backed by the supplied mutable list.
     *
     * @param contacts Mutable list of contacts to manage.
     */
    public ContactList(List<Contact> contacts) {
        assert contacts != null : "contact list must be present";
        for (Contact contact : contacts) {
            assert contact != null : "contact list must not contain null contacts";
        }
        this.contacts = contacts;
    }

    /**
     * Returns the number of contacts in this list.
     *
     * @return Number of contacts.
     */
    public int size() {
        return this.contacts.size();
    }

    /**
     * Adds a contact and returns its display text.
     *
     * @param name Contact's name.
     * @param phone Contact's phone number.
     * @param email Contact's email address.
     * @return Display text of the added contact.
     */
    public String addContact(String name, String phone, String email) {
        Contact contact = new Contact(name, phone, email);
        this.contacts.add(contact);
        return contact.toString();
    }

    /**
     * Removes the contact at {@code index} and returns its display text.
     *
     * @param index Zero-based index of the contact to remove.
     * @return Display text of the removed contact.
     * @throws LabyException If the index does not identify a contact.
     */
    public String deleteContact(int index) throws LabyException {
        try {
            Contact contact = this.contacts.get(index);
            this.contacts.remove(index);
            return contact.toString();
        } catch (IndexOutOfBoundsException e) {
            throw new LabyException("please enter a valid contact index.");
        }
    }

    /**
     * Returns all contacts numbered in the format shown in the console.
     *
     * @return Numbered display representation of all contacts.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.contacts.size(); i++) {
            result.append(i + 1).append(".").append(this.contacts.get(i)).append("\n");
        }
        return result.toString();
    }

    /**
     * Returns all contacts in the format used for persistence.
     *
     * @return Serialized representation of all contacts.
     */
    public String toFileString() {
        return this.contacts.stream()
                .map(contact -> contact.toFileString() + "\n")
                .collect(Collectors.joining());
    }
}
