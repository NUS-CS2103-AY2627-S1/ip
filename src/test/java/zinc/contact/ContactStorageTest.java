package zinc.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests contact persistence. */
public class ContactStorageTest {
    private static final Path STORAGE_FILE = Path.of("data", "zincContacts.txt");

    @BeforeEach
    @AfterEach
    public void clearStorage() throws Exception {
        Files.deleteIfExists(STORAGE_FILE);
    }

    @Test
    public void saveContacts_thenLoadContacts_restoresAllFields() throws Exception {
        ContactStorage contactStorage = new ContactStorage();
        contactStorage.saveContacts(List.of(new Contact("Tom Tan", "91234567", "Friend | neighbor")));

        Contact loadedContact = contactStorage.loadContacts().get(0);

        assertEquals("Tom Tan | 91234567 | Friend | neighbor", Files.readString(STORAGE_FILE).trim());
        assertEquals("Tom Tan", loadedContact.getName());
        assertEquals("91234567", loadedContact.getPhoneNumber());
        assertEquals("Friend | neighbor", loadedContact.getDescription());
        assertFalse(contactStorage.wasDataCorrupted());
    }

    @Test
    public void loadContacts_withMalformedFile_returnsEmptyListAndReportsCorruption() throws Exception {
        Files.createDirectories(STORAGE_FILE.getParent());
        Files.writeString(STORAGE_FILE, "Invalid contact");
        ContactStorage contactStorage = new ContactStorage();

        List<Contact> loadedContacts = contactStorage.loadContacts();

        assertTrue(loadedContacts.isEmpty());
        assertTrue(Files.readAllLines(STORAGE_FILE).isEmpty());
        assertTrue(contactStorage.wasDataCorrupted());
    }
}
