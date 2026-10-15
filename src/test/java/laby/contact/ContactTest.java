package laby.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import laby.Parser;

/** Tests contact display and serialization behavior. */
class ContactTest {

    @Test
    void contact_gettersAndFormats_returnContactDetails() {
        Contact contact = new Contact(" John Doe ", " 91234567 ", " john@example.com ");

        assertEquals("John Doe", contact.getName());
        assertEquals("91234567", contact.getPhone());
        assertEquals("john@example.com", contact.getEmail());
        assertEquals("John Doe | Phone: 91234567 | Email: john@example.com", contact.toString());
        assertEquals("C" + Parser.FIELD_SEPARATOR + "John Doe" + Parser.FIELD_SEPARATOR
                + "91234567" + Parser.FIELD_SEPARATOR + "john@example.com", contact.toFileString());
    }
}
