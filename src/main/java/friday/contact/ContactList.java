package friday.contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Owns contacts in insertion order, enforcing unique names and one-based contact numbers.
 * Contact operations never read or modify task state or storage.
 */
public final class ContactList {
    private final List<Contact> contacts = new ArrayList<>();

    /** Creates an empty contact list. */
    public ContactList() {
        this(List.of());
    }

    /** Copies and validates initial contacts, rejecting duplicate names. */
    public ContactList(List<Contact> initialContacts) {
        for (Contact contact : initialContacts) {
            add(contact);
        }
    }

    public int size() {
        return contacts.size();
    }

    /** Adds a contact only if its name is not already present, ignoring case. */
    public void add(Contact contact) {
        Objects.requireNonNull(contact);
        if (contacts.stream().anyMatch(existing -> existing.getNameKey().equals(contact.getNameKey()))) {
            throw new IllegalArgumentException("A contact with this name already exists.");
        }
        contacts.add(contact);
    }

    /** Returns the contact at a valid one-based number. */
    public Contact get(int contactNumber) {
        return contacts.get(toIndex(contactNumber));
    }

    /** Removes a contact and shifts later contacts down by one number. */
    public Contact delete(int contactNumber) {
        return contacts.remove(toIndex(contactNumber));
    }

    /** Returns matching original contact numbers without changing the collection. */
    public List<Integer> findContactNumbersContaining(String keyword) {
        String trimmedKeyword = keyword.strip();
        if (trimmedKeyword.isBlank()) {
            throw new IllegalArgumentException("Invalid contact find format. Use: contact find KEYWORD");
        }
        List<Integer> matches = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).contains(trimmedKeyword)) {
                matches.add(i + 1);
            }
        }
        return matches;
    }

    /** Returns an immutable snapshot; contact objects are immutable as well. */
    public List<Contact> toList() {
        return List.copyOf(contacts);
    }

    /** Validates contact numbers before converting to a zero-based index. */
    private int toIndex(int contactNumber) {
        if (contactNumber < 1 || contactNumber > contacts.size()) {
            throw new IllegalArgumentException("The contact number is invalid.");
        }
        return contactNumber - 1;
    }
}
