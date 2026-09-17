package notmarth.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import notmarth.exception.NotMarthException;
import org.junit.jupiter.api.Test;

/** Tests contact validation and contact-list operations. */
class ContactTest {
    /** Verifies that contact fields and their display form are preserved. */
    @Test
    void contactCreation_validFields_preservesValuesAndDisplay() {
        Contact contact = new Contact("Mrs Tan", "81234567", "Engage Road");

        assertEquals("Mrs Tan", contact.getName());
        assertEquals("81234567", contact.getPhone());
        assertEquals("Engage Road", contact.getAddress());
        assertEquals("Mrs Tan (phone: 81234567, address: Engage Road)", contact.toString());
    }

    /** Verifies that blank or null contact fields are rejected. */
    @Test
    void contactCreation_missingField_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Contact(null, "1", "road"));
        assertThrows(IllegalArgumentException.class, () -> new Contact("name", " ", "road"));
        assertThrows(IllegalArgumentException.class, () -> new Contact("name", "1", null));
    }

    /** Verifies that contacts can be added, read, and exposed without mutation. */
    @Test
    void contactList_validContacts_preservesOrderAndReadOnlyView() throws NotMarthException {
        Contact first = new Contact("First", "1", "Road 1");
        Contact second = new Contact("Second", "2", "Road 2");
        ContactList contacts = new ContactList(List.of(first));

        contacts.add(second);

        assertEquals(2, contacts.size());
        assertEquals(first, contacts.get(0));
        assertEquals(second, contacts.asList().get(1));
        assertThrows(UnsupportedOperationException.class, () -> contacts.asList().clear());
    }

    /** Verifies that invalid contact deletion reports empty and out-of-range states. */
    @Test
    void contactList_invalidDeletion_throwsNotMarthException() throws NotMarthException {
        ContactList empty = new ContactList();
        assertThrows(NotMarthException.class, () -> empty.delete(1));

        ContactList contacts = new ContactList(List.of(new Contact("First", "1", "Road 1")));
        assertThrows(NotMarthException.class, () -> contacts.delete(0));
        assertThrows(NotMarthException.class, () -> contacts.delete(2));
    }

    /** Verifies that a contact list cannot be constructed without a source collection. */
    @Test
    void contactList_nullInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ContactList(null));
    }
}
