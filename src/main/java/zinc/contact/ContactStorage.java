package zinc.contact;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves contacts to, and restores contacts from, Zinc's local contact file.
 */
public class ContactStorage {
    /** The separator between fields in the contact-storage format. */
    private static final String FIELD_SEPARATOR = " | ";

    /** The regular expression used to split stored contact fields. */
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";

    /** The number of fields required in a stored contact. */
    private static final int FIELD_COUNT = 3;

    /** The field index containing the contact name. */
    private static final int NAME_INDEX = 0;

    /** The field index containing the contact phone number. */
    private static final int PHONE_NUMBER_INDEX = 1;

    /** The field index containing the contact description. */
    private static final int DESCRIPTION_INDEX = 2;

    /** The file used to retain contacts between application runs. */
    private static final Path STORAGE_FILE = Path.of("data", "zincContacts.txt");

    /** Whether the most recent load found malformed contact data. */
    private boolean wasDataCorrupted;

    /**
     * Returns all contacts currently saved in the contact file.
     *
     * @return The saved contacts in their original order.
     */
    public List<Contact> loadContacts() {
        wasDataCorrupted = false;
        List<Contact> contacts = new ArrayList<>();
        if (!Files.exists(STORAGE_FILE)) {
            return contacts;
        }

        try {
            Files.readAllLines(STORAGE_FILE).stream()
                    .map(this::parseContact)
                    .forEach(contacts::add);
        } catch (IllegalArgumentException exception) {
            wasDataCorrupted = true;
            System.out.println("Saved contact file has an invalid layout. Clearing saved contacts.\n");
            resetStorage();
            contacts.clear();
        } catch (IOException exception) {
            System.out.println("Unable to load saved contacts. Starting with an empty list.\n");
            resetStorage();
        }
        return contacts;
    }

    /** Returns whether the most recent load found malformed contact data. */
    boolean wasDataCorrupted() {
        return wasDataCorrupted;
    }

    /**
     * Writes every supplied contact to the contact file.
     *
     * @param contacts The contacts to save.
     */
    public void saveContacts(List<Contact> contacts) {
        assert contacts != null : "Contacts to save must not be null";
        List<String> lines = contacts.stream()
                .map(this::formatContact)
                .toList();

        try {
            Files.createDirectories(STORAGE_FILE.getParent());
            Files.write(STORAGE_FILE, lines);
        } catch (IOException exception) {
            System.out.println("Unable to save contacts. Error: " + exception + "\n");
        }
    }

    /** Converts a stored line into a contact. */
    private Contact parseContact(String line) {
        String[] storedFields = line.split(FIELD_SEPARATOR_REGEX, FIELD_COUNT);
        if (storedFields.length != FIELD_COUNT) {
            throw new IllegalArgumentException("Incorrect number of contact fields");
        }

        String name = storedFields[NAME_INDEX];
        String phoneNumber = storedFields[PHONE_NUMBER_INDEX];
        String description = storedFields[DESCRIPTION_INDEX];
        if (name.isBlank() || (!phoneNumber.isEmpty() && !Contact.isValidPhoneNumber(phoneNumber))) {
            throw new IllegalArgumentException("Invalid contact details");
        }
        return new Contact(name, phoneNumber, description);
    }

    /** Converts a contact into the storage format. */
    private String formatContact(Contact contact) {
        return contact.getName() + FIELD_SEPARATOR
                + contact.getPhoneNumber() + FIELD_SEPARATOR
                + contact.getDescription();
    }

    /** Clears an invalid contact file. */
    private void resetStorage() {
        try {
            Files.write(STORAGE_FILE, List.of());
        } catch (IOException exception) {
            System.out.println("Unable to clear the invalid saved contact file.\n");
        }
    }
}
