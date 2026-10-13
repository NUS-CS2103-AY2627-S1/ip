package ruby.contact;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ruby.RubyException;

/**
 * Stores the contacts managed by Ruby.
 */
public class ContactList {
    private final List<Contact> contacts;

    /**
     * Creates an empty contact list.
     */
    public ContactList() {
        this.contacts = new ArrayList<>();
    }

    /**
     * Adds a contact to the end of this list.
     *
     * @param contact Contact to add.
     * @return Confirmation containing the contact and updated contact count.
     */
    public String addContact(Contact contact) throws RubyException {
        assert contact != null : "Contact cannot be null";
        if (contacts.stream().anyMatch(contact::hasSameDetails)) {
            throw new RubyException("That contact is already in your circle.");
        }
        contacts.add(contact);
        int count = contacts.size();
        String noun = count == 1 ? "contact" : "contacts";
        return "Saved. I never forget a name:\n  "
                + contact
                + "\nThat's " + count + " " + noun + " in your circle.";
    }

    /**
     * Removes and returns confirmation for the contact at a zero-based index.
     *
     * @param index Zero-based index of the contact.
     * @return Confirmation describing the removed contact and remaining count.
     * @throws RubyException If the index does not identify an existing contact.
     */
    public String deleteContact(int index) throws RubyException {
        assert index >= 0 : "Parser only produces non-negative contact indexes";
        Contact contact = getContact(index, "delete");
        contacts.remove(index);

        int count = contacts.size();
        String noun = count == 1 ? "contact" : "contacts";
        return "Removed from your circle:\n  "
                + contact
                + "\nThat leaves " + count + " " + noun + " in your circle.";
    }

    /**
     * Formats all stored contacts as a one-based numbered list.
     *
     * @return Numbered contacts, or a message when there are no contacts.
     */
    public String listContacts() {
        if (contacts.isEmpty()) {
            return "No contacts yet — a fresh, unpolished page.";
        }
        return "Your circle, as requested:"
                + IntStream.range(0, contacts.size())
                        .mapToObj(i -> "\n" + (i + 1) + ". " + contacts.get(i))
                        .collect(Collectors.joining());
    }

    /**
     * Formats every contact as one line of the saved data file.
     *
     * @return One contact per line, or an empty string when there are no
     *         contacts.
     */
    public String toDataString() {
        return contacts.stream()
                .map(Contact::toDataString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Retrieves a contact after checking that the requested index exists.
     *
     * @param index  Zero-based index of the contact.
     * @param action Action requested by the user, used in error messages.
     * @return Contact at the specified index.
     * @throws RubyException If the index does not identify an existing contact.
     */
    private Contact getContact(int index, String action) throws RubyException {
        if (contacts.isEmpty()) {
            throw new RubyException("You have no contacts to " + action + " — add one first.");
        }
        if (index < 0 || index >= contacts.size()) {
            throw new RubyException(
                    "Contact " + (index + 1)
                            + "? You only have " + contacts.size()
                            + ". Pick a number from 1 to " + contacts.size() + ".");
        }
        assert contacts.get(index) != null : "Contact at index " + index + " should not be null";
        return contacts.get(index);
    }
}
