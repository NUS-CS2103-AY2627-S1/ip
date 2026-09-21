package friday.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import friday.contact.Contact;
import friday.contact.ContactList;

/**
 * Persists independent UTF-8 contact snapshots using escaped, four-field records.
 * Malformed files are rejected completely; saving replaces the destination only after a full write.
 */
public final class ContactStorage {
    private final Path file;

    /** Creates storage for the supplied contact file. */
    public ContactStorage(Path file) {
        this.file = file;
    }

    /**
     * Loads contacts in file order, returning an empty list when the file does not exist.
     *
     * @throws IOException if reading fails or any record is invalid or duplicates an earlier name.
     */
    public List<Contact> load() throws IOException {
        ContactList contacts = new ContactList();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    List<String> fields = splitFields(line);
                    if (fields.size() != 4 || !fields.getFirst().equals("C")) {
                        throw new IllegalArgumentException("Invalid contact record.");
                    }
                    contacts.add(new Contact(fields.get(1), fields.get(2), fields.get(3)));
                } catch (IllegalArgumentException e) {
                    throw new IOException("Invalid contact record on line " + lineNumber + ".", e);
                }
            }
        } catch (NoSuchFileException e) {
            return List.of();
        }
        return contacts.toList();
    }

    /**
     * Writes a complete, validated snapshot before replacing the existing file.
     *
     * @throws IOException if writing or replacing the destination fails.
     * @throws IllegalArgumentException if the snapshot contains duplicate names.
     */
    public void save(List<Contact> contacts) throws IOException {
        List<Contact> snapshot = new ContactList(contacts).toList();
        Path directory = createSaveDirectory();
        Path temporaryFile = Files.createTempFile(directory, "friday-contacts-", ".tmp");
        try {
            writeContacts(temporaryFile, snapshot);
            replaceSaveFile(temporaryFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /** Creates and returns the folder that will contain the contact save file. */
    private Path createSaveDirectory() throws IOException {
        Path directory = file.getParent();
        if (directory == null) {
            directory = Path.of(".");
        }
        Files.createDirectories(directory);
        return directory;
    }

    /** Writes each contact as one encoded record in the temporary snapshot. */
    private void writeContacts(Path temporaryFile, List<Contact> contacts) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
            for (Contact contact : contacts) {
                writer.write("C|" + escape(contact.getName()) + "|" + escape(contact.getPhone())
                        + "|" + escape(contact.getEmail()));
                writer.newLine();
            }
        }
    }

    /** Replaces the destination atomically when supported, otherwise using a regular replacement move. */
    private void replaceSaveFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Escapes backslashes first so newly added delimiter escapes are not escaped again. */
    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /** Decodes only the two supported escapes and retains empty optional fields. */
    private static List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;
        for (char character : line.toCharArray()) {
            if (isEscaped) {
                if (character != '\\' && character != '|') {
                    throw new IllegalArgumentException("Invalid contact escape.");
                }
                field.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (isEscaped) {
            throw new IllegalArgumentException("Incomplete contact escape.");
        }
        fields.add(field.toString());
        return fields;
    }
}
