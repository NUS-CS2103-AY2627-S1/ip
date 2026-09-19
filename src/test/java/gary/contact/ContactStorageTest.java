package gary.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ContactStorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void loadContacts_fileMissing_returnsEmptyList() throws IOException {
        ContactStorage storage = new ContactStorage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.loadContacts().isEmpty());
    }

    @Test
    void saveAndLoadContacts_mixedRecords_preservesValidContacts() throws IOException {
        Path filePath = temporaryDirectory.resolve("data").resolve("contacts.txt");
        ContactStorage storage = new ContactStorage(filePath);
        List<Contact> contacts = List.of(
                new Contact("Alice Tan", "91234567", "alice@example.com"),
                new Contact("Bob Lim", "87654321", "bob@example.com"));

        storage.saveContacts(contacts);
        Files.writeString(
                filePath,
                System.lineSeparator() + "corrupted record",
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
        ArrayList<Contact> loadedContacts = storage.loadContacts();

        assertEquals(2, loadedContacts.size());
        assertEquals("Alice Tan (Phone: 91234567, Email: alice@example.com)",
                loadedContacts.get(0).toString());
        assertEquals("Bob Lim (Phone: 87654321, Email: bob@example.com)",
                loadedContacts.get(1).toString());
    }
}
