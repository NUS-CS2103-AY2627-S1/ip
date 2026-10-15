package zinc.contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import zinc.ui.Ui;

/**
 * Maintains and persists the user's contacts.
 */
public class ContactList {
    /** The stored contacts. */
    private final List<Contact> contacts = new ArrayList<>();

    /** The component responsible for persisting contacts. */
    private final ContactStorage contactStorage = new ContactStorage();

    /** Whether malformed contact data was found while restoring saved contacts. */
    private final boolean wasStoredDataCorrupted;

    /** The UI used to display contact-list messages. */
    private final Ui ui;

    /** Creates a contact list and restores contacts saved by a previous run. */
    public ContactList() {
        this(new Ui());
    }

    /**
     * Creates a contact list that displays messages through the supplied UI.
     *
     * @param ui The UI used to display contact-list messages.
     */
    public ContactList(Ui ui) {
        assert ui != null : "Contact-list UI must not be null";
        this.ui = ui;
        contacts.addAll(contactStorage.loadContacts());
        wasStoredDataCorrupted = contactStorage.wasDataCorrupted();
    }

    /**
     * Adds and saves a contact.
     *
     * @param contact The contact to add.
     */
    public void addContact(Contact contact) {
        assert contact != null : "Contact to add must not be null";
        contacts.add(contact);
        saveContacts();
        ui.printContactAdded(contact.toString());
    }

    /**
     * Deletes and saves the first contact whose name exactly matches the supplied name.
     *
     * @param name The name of the contact to delete.
     */
    public void deleteContact(String name) {
        int contactIndex = findContactIndex(name);
        if (contactIndex < 0) {
            ui.printContactNotFound(name);
            return;
        }

        Contact deletedContact = contacts.remove(contactIndex);
        saveContacts();
        ui.printContactDeleted(deletedContact.toString());
    }

    /**
     * Updates and saves the first contact whose name exactly matches the supplied name.
     * A {@code null} replacement retains that field's current value.
     *
     * @param currentName The current name used to locate the contact.
     * @param updatedName The replacement name, or {@code null} to retain it.
     * @param updatedPhoneNumber The replacement phone number, or {@code null} to retain it.
     * @param updatedDescription The replacement description, or {@code null} to retain it.
     */
    public void updateContact(String currentName, String updatedName, String updatedPhoneNumber,
                              String updatedDescription) {
        int contactIndex = findContactIndex(currentName);
        if (contactIndex < 0) {
            ui.printContactNotFound(currentName);
            return;
        }

        Contact existingContact = contacts.get(contactIndex);
        String replacementName = updatedName == null ? existingContact.getName() : updatedName;
        String replacementPhoneNumber = updatedPhoneNumber == null
                ? existingContact.getPhoneNumber() : updatedPhoneNumber;
        String replacementDescription = updatedDescription == null
                ? existingContact.getDescription() : updatedDescription;
        Contact updatedContact = existingContact.withUpdatedDetails(replacementName, replacementPhoneNumber,
                replacementDescription);
        contacts.set(contactIndex, updatedContact);
        saveContacts();
        ui.printContactUpdated(updatedContact.toString());
    }

    /**
     * Returns a defensive copy of the stored contacts.
     *
     * @return All stored contacts.
     */
    public List<Contact> getContacts() {
        return new ArrayList<>(contacts);
    }

    /** Prints all contacts currently stored. */
    public void printContacts() {
        String heading = contacts.isEmpty() ? "You have no contacts yet." : "Here are your contacts:";
        ui.printContactList(heading, contacts.stream().map(Contact::toString).toList());
    }

    /**
     * Prints all contacts whose names contain the supplied keyword.
     *
     * @param keyword The case-insensitive keyword to search for.
     */
    public void printContactsContaining(String keyword) {
        assert keyword != null : "Contact search keyword must not be null";
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<String> matchingContacts = contacts.stream()
                .filter(contact -> contact.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .map(Contact::toString)
                .toList();
        String heading = matchingContacts.isEmpty()
                ? "No contacts containing \"" + keyword + "\" found."
                : "Here are the contacts containing \"" + keyword + "\":";
        ui.printContactList(heading, matchingContacts);
    }
    /**
     * Returns the number of stored contacts.
     *
     * @return The contact count.
     */
    public int getContactCount() {
        return contacts.size();
    }

    /** Returns whether a contact has the supplied exact, case-sensitive name. */
    boolean containsContactNamed(String name) {
        assert name != null : "Contact name must not be null";
        return findContactIndex(name) >= 0;
    }

    /**
     * Returns whether malformed contact data was found during construction.
     *
     * @return Whether the saved contact data was corrupted.
     */
    public boolean wasStoredDataCorrupted() {
        return wasStoredDataCorrupted;
    }

    /** Finds the first contact with an exact, case-sensitive name match. */
    private int findContactIndex(String name) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /** Saves the current contact list. */
    private void saveContacts() {
        contactStorage.saveContacts(contacts);
    }
}
