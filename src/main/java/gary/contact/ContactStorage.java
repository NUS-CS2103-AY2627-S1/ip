package gary.contact;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Loads contacts from and saves contacts to a text file.
 */
public class ContactStorage {
    private static final int NAME_INDEX = 0;
    private static final int PHONE_INDEX = 1;
    private static final int EMAIL_INDEX = 2;
    private static final int FIELD_COUNT = 3;

    private final Path filePath;

    /**
     * Creates a storage manager for the specified contact data file.
     *
     * @param filePath Path of the contact data file.
     */
    public ContactStorage(Path filePath) {
        assert filePath != null : "Contact file path should not be null";

        this.filePath = filePath;
    }

    /**
     * Loads valid contacts from the data file.
     *
     * @return Contacts represented by valid records, or an empty list if the file does not exist.
     * @throws IOException If the data file cannot be read.
     */
    public ArrayList<Contact> loadContacts() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        return Files.readAllLines(filePath, StandardCharsets.UTF_8).stream()
                .map(this::parseContact)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Saves the contacts, creating the data directory when needed.
     *
     * @param contacts Contacts to save.
     * @throws IOException If the contacts cannot be written.
     */
    public void saveContacts(List<Contact> contacts) throws IOException {
        assert contacts != null : "Contact list should not be null";
        assert contacts.stream().noneMatch(contact -> contact == null)
                : "Contact list should not contain null contacts";

        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = contacts.stream()
                .map(this::formatContact)
                .toList();
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    private Contact parseContact(String line) {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length != FIELD_COUNT
                || fields[NAME_INDEX].isBlank()
                || fields[PHONE_INDEX].isBlank()
                || fields[EMAIL_INDEX].isBlank()) {
            return null;
        }
        return new Contact(fields[NAME_INDEX], fields[PHONE_INDEX], fields[EMAIL_INDEX]);
    }

    private String formatContact(Contact contact) {
        assert contact != null : "Contact to format should not be null";

        return String.join(" | ", contact.getName(), contact.getPhone(), contact.getEmail());
    }
}
