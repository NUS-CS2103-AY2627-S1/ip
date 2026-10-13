package ruby.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ruby.RubyException;

class ContactListTest {

    @Test
    void addContact_newList_reportsContactAndCount() throws RubyException {
        ContactList contacts = new ContactList();

        String response = contacts.addContact(new Contact("John Doe", "91234567", "john@example.com", ""));

        assertEquals("Saved. I never forget a name:\n"
                + "  John Doe | 91234567 | john@example.com\n"
                + "That's 1 contact in your circle.", response);
    }

    @Test
    void addContact_duplicateContact_throws() throws RubyException {
        ContactList contacts = new ContactList();
        contacts.addContact(new Contact("John Doe", "91234567", "john@example.com", ""));
        assertThrows(RubyException.class, () ->
                contacts.addContact(new Contact("John Doe", "91234567", "john@example.com", "")));
    }

    @Test
    void deleteContact_existingIndex_removesContactAndReportsCount() throws RubyException {
        ContactList contacts = new ContactList();
        contacts.addContact(new Contact("John", "", "", ""));
        contacts.addContact(new Contact("Jane", "91234567", "", ""));

        String response = contacts.deleteContact(0);

        assertEquals("Removed from your circle:\n"
                + "  John\n"
                + "That leaves 1 contact in your circle.", response);
        assertEquals("Your circle, as requested:\n"
                + "1. Jane | 91234567", contacts.listContacts());
    }

    @Test
    void deleteContact_emptyList_throws() {
        ContactList contacts = new ContactList();

        RubyException exception = assertThrows(RubyException.class, () -> contacts.deleteContact(0));
        assertEquals("You have no contacts to delete — add one first.", exception.getMessage());
    }

    @Test
    void deleteContact_outOfRangeIndex_throws() throws RubyException {
        ContactList contacts = new ContactList();
        contacts.addContact(new Contact("John", "", "", ""));

        RubyException exception = assertThrows(RubyException.class, () -> contacts.deleteContact(1));
        assertEquals("Contact 2? You only have 1. Pick a number from 1 to 1.", exception.getMessage());
    }

    @Test
    void deleteContact_lastContact_usesPluralZeroCount() throws RubyException {
        ContactList contacts = new ContactList();
        contacts.addContact(new Contact("John", "", "", ""));

        assertEquals("Removed from your circle:\n  John\nThat leaves 0 contacts in your circle.",
                contacts.deleteContact(0));
    }

    @Test
    void listContacts_emptyList_returnsNoContactsMessage() {
        ContactList contacts = new ContactList();

        assertEquals("No contacts yet — a fresh, unpolished page.", contacts.listContacts());
    }

    @Test
    void listContacts_multipleContacts_returnsNumberedList() throws RubyException {
        ContactList contacts = new ContactList();
        contacts.addContact(new Contact("John", "", "", ""));
        contacts.addContact(new Contact("Jane", "91234567", "jane@example.com", ""));

        assertEquals("Your circle, as requested:\n"
                + "1. John\n"
                + "2. Jane | 91234567 | jane@example.com", contacts.listContacts());
    }

    @Test
    void toDataString_multipleContacts_returnsOneLinePerContact() throws RubyException {
        ContactList contacts = new ContactList();
        contacts.addContact(new Contact("John", "", "", ""));
        contacts.addContact(new Contact("Jane", "91234567", "", ""));

        assertEquals("C | John |  |  | \nC | Jane | 91234567 |  | ", contacts.toDataString());
    }
}
