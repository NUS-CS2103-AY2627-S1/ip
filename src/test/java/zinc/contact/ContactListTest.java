package zinc.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests adding, updating, and deleting contacts. */
public class ContactListTest {
    private static final Path STORAGE_FILE = Path.of("data", "zincContacts.txt");

    @BeforeEach
    @AfterEach
    public void clearStorage() throws Exception {
        Files.deleteIfExists(STORAGE_FILE);
    }

    @Test
    public void addContact_withOptionalFields_success() {
        ContactList contactList = new ContactList();

        contactList.addContact(new Contact("Tom", "", ""));

        assertEquals("Tom", contactList.getContacts().get(0).getName());
        assertEquals("", contactList.getContacts().get(0).getPhoneNumber());
    }

    @Test
    public void update_existingContact_success() {
        ContactList contactList = new ContactList();
        contactList.addContact(new Contact("Tom", "91234567", "Friend"));

        contactList.updateContact("Tom", "Tommy", "94449999", "Best friend");

        Contact updatedContact = contactList.getContacts().get(0);
        assertEquals("Tommy", updatedContact.getName());
        assertEquals("94449999", updatedContact.getPhoneNumber());
        assertEquals("Best friend", updatedContact.getDescription());
    }

    @Test
    public void delete_existingContact_success() {
        ContactList contactList = new ContactList();
        contactList.addContact(new Contact("Tom", "91234567", "Friend"));

        contactList.deleteContact("Tom");

        assertEquals(0, contactList.getContactCount());
    }

    @Test
    public void printContactsContaining_caseInsensitiveSubstring_printsMatchingContacts() {
        ContactList contactList = new ContactList();
        contactList.addContact(new Contact("Anna", "", "Friend"));
        contactList.addContact(new Contact("Joanna", "", "Colleague"));
        contactList.addContact(new Contact("Tom", "", "Family"));

        String output = captureOutput(() -> contactList.printContactsContaining("ANN"));

        assertTrue(output.contains("Name: Anna"));
        assertTrue(output.contains("Name: Joanna"));
        assertFalse(output.contains("Name: Tom"));
    }

    @Test
    public void printContactsContaining_noMatches_printsNoMatchMessage() {
        ContactList contactList = new ContactList();
        contactList.addContact(new Contact("Anna", "", "Friend"));

        String output = captureOutput(() -> contactList.printContactsContaining("Zed"));

        assertTrue(output.contains("No contacts containing \"Zed\" found."));
    }

    /** Captures console output produced by a contact-list display operation. */
    private String captureOutput(Runnable action) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        System.setOut(new PrintStream(output));
        try {
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
