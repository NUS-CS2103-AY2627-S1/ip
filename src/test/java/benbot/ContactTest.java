package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests contact parsing and formatting. */
class ContactTest {
    @Test
    void createFromCommand_validDetails_createsContact() throws InvalidCommandException {
        Contact contact = Contact.createFromCommand(new String[] {
            "contact", "Alice", "Tan", "/phone", "91234567", "/email", "alice@example.com"
        });

        assertEquals("[C] Alice Tan (phone: 91234567; email: alice@example.com)",
                contact.toString());
        assertEquals("contact Alice Tan /phone 91234567 /email alice@example.com",
                contact.toStorageString());
    }

    @Test
    void createFromCommand_missingEmail_throwsException() {
        String[] words = {"contact", "Alice", "/phone", "91234567"};

        InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                Contact.createFromCommand(words));

        assertEquals("Use: " + Contact.USAGE, exception.getMessage());
    }

    @Test
    void createFromCommand_invalidStructure_throwsUsageException() {
        String[][] invalidCommands = {
            {"contact", "/phone", "91234567", "/email", "alice@example.com"},
            {"contact", "Alice", "/email", "alice@example.com"},
            {"contact", "Alice", "/email", "alice@example.com", "/phone", "91234567"},
            {"contact", "Alice", "/phone", "/email", "alice@example.com"},
            {"contact", "Alice", "/phone", "91234567", "/email"},
            {"contact", "Alice", "/phone", "91234567", "/phone", "87654321",
                "/email", "alice@example.com"},
            {"contact", "Alice", "/phone", "91234567", "/email", "alice@example.com",
                "/email", "other@example.com"},
            {"contact", "Alice", "/phone", "91234567", "/email", "alice@example.com", "extra"}
        };

        for (String[] words : invalidCommands) {
            InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                    Contact.createFromCommand(words));
            assertEquals("Use: " + Contact.USAGE, exception.getMessage());
        }
    }

    @Test
    void contact_missingField_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                new Contact(null, "91234567", "alice@example.com"));
        assertThrows(AssertionError.class, () ->
                new Contact("Alice", " ", "alice@example.com"));
        assertThrows(AssertionError.class, () ->
                new Contact("Alice", "91234567", null));
    }
}
