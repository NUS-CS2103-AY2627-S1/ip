package laby.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import laby.LabyException;
import laby.Parser;

/** Tests the public behavior of {@link ContactList}. */
class ContactListTest {

    @Test
    void addContact_andToString_returnsNumberedContact() {
        ContactList contactList = new ContactList(new ArrayList<>());

        String addedContact = contactList.addContact("John Doe", "91234567", "john@example.com");

        assertEquals("John Doe | Phone: 91234567 | Email: john@example.com", addedContact);
        assertEquals("1.John Doe | Phone: 91234567 | Email: john@example.com\n",
                contactList.toString());
    }

    @Test
    void deleteContact_validIndex_removesAndReturnsContact() throws LabyException {
        ContactList contactList = new ContactList(new ArrayList<>());
        contactList.addContact("John Doe", "91234567", "john@example.com");
        contactList.addContact("Jane Tan", "98765432", "jane@example.com");

        String deletedContact = contactList.deleteContact(0);

        assertEquals("John Doe | Phone: 91234567 | Email: john@example.com", deletedContact);
        assertEquals("1.Jane Tan | Phone: 98765432 | Email: jane@example.com\n",
                contactList.toString());
    }

    @Test
    void deleteContact_invalidIndex_throwsException() {
        ContactList contactList = new ContactList(new ArrayList<>());

        LabyException exception = assertThrows(LabyException.class,
                () -> contactList.deleteContact(0));

        assertEquals("please enter a valid contact index.", exception.getMessage());
    }

    @Test
    void toFileString_multipleContacts_returnsStorageFormat() {
        ContactList contactList = new ContactList(new ArrayList<>());
        contactList.addContact("John Doe", "91234567", "john@example.com");
        contactList.addContact("Jane Tan", "98765432", "jane@example.com");

        assertEquals("C" + Parser.FIELD_SEPARATOR + "John Doe" + Parser.FIELD_SEPARATOR
                        + "91234567" + Parser.FIELD_SEPARATOR + "john@example.com\n"
                        + "C" + Parser.FIELD_SEPARATOR + "Jane Tan" + Parser.FIELD_SEPARATOR
                        + "98765432" + Parser.FIELD_SEPARATOR + "jane@example.com\n",
                contactList.toFileString());
    }
}
