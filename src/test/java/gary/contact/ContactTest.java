package gary.contact;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ContactTest {
    @Test
    void containsKeyword_variedDetails_matchesIgnoringCase() {
        Contact contact = new Contact("Alice Tan", "91234567", "alice@example.com");

        assertTrue(contact.containsKeyword("ALICE"));
        assertTrue(contact.containsKeyword("2345"));
        assertTrue(contact.containsKeyword("EXAMPLE.COM"));
        assertFalse(contact.containsKeyword("Bob"));
    }
}
