package ff15.contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ff15.FF15Exception;

/**
 * Holds the contacts the user is keeping details of, and the operations that add
 * to, remove from, and search that collection.
 *
 * <p>Contact numbers are 1-based here, matching what the user types and what the
 * list prints, so the conversion to the ArrayList's 0-based index happens inside
 * this class instead of at every call site.
 */
public class ContactList {
    private final ArrayList<Contact> contacts;

    /** Creates an empty list, used when there is nothing saved to start from. */
    public ContactList() {
        this.contacts = new ArrayList<>();
    }

    /** Creates a list holding {@code contacts}, typically the ones just read from disk. */
    public ContactList(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    /** Returns how many contacts the list holds. */
    public int size() {
        return contacts.size();
    }

    /** Adds {@code contactsToAdd} to the end of the list, keeping the order given. */
    public void add(Contact... contactsToAdd) {
        // Varargs makes add() with no arguments legal, which would silently do nothing.
        assert contactsToAdd.length > 0 : "add was called with no contacts to add";
        Collections.addAll(contacts, contactsToAdd);
    }

    /**
     * Returns the contact the user knows as {@code number}, counting from 1.
     *
     * @throws FF15Exception if there is no such contact.
     */
    public Contact get(int number) throws FF15Exception {
        checkNumber(number);
        int index = number - 1;
        assert index >= 0 && index < contacts.size()
                : "checkNumber let through an out-of-range contact number: " + number;
        return contacts.get(index);
    }

    /**
     * Removes and returns the contact the user knows as {@code number}, counting from 1.
     *
     * @throws FF15Exception if there is no such contact.
     */
    public Contact delete(int number) throws FF15Exception {
        checkNumber(number);
        int index = number - 1;
        assert index >= 0 && index < contacts.size()
                : "checkNumber let through an out-of-range contact number: " + number;
        return contacts.remove(index);
    }

    /** Rejects a contact number that does not name a contact in this list. */
    private void checkNumber(int number) throws FF15Exception {
        if (number < 1 || number > contacts.size()) {
            throw new FF15Exception("Contact " + number + "? There are " + contacts.size()
                    + ". And I know all of them personally.");
        }
    }

    /**
     * Returns the contacts whose name contains {@code keyword}, kept in list
     * order. Only the name is searched, so a phone number or an email address is
     * never matched.
     *
     * @param keyword the text the user is searching for.
     * @return the matching contacts, empty when nothing matches.
     */
    public List<Contact> find(String keyword) {
        // Collected into an ArrayList rather than with toList(), because callers are
        // handed a copy they are free to modify.
        return contacts.stream()
                .filter(contact -> contact.hasKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the number of the contact in this list that is the same as
     * {@code contact}, counting from 1, or nothing if there is no such contact.
     * This is what lets an add refuse a repeat and say which one it repeats.
     */
    public OptionalInt findSame(Contact contact) {
        return IntStream.range(0, contacts.size())
                .filter(i -> contacts.get(i).isSameAs(contact))
                .map(i -> i + 1)
                .findFirst();
    }

    /**
     * Returns the contacts for reading only, e.g. to print them or write them to
     * disk. The view cannot be modified, so nothing outside this class can change
     * the list behind its back.
     */
    public List<Contact> asList() {
        return Collections.unmodifiableList(contacts);
    }
}
