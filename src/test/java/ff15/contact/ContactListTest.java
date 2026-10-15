package ff15.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ff15.FF15Exception;

/**
 * Tests the contact list's 1-based numbering, its guards against numbers that do
 * not name a contact, and the keyword search behind {@code contact find}.
 */
public class ContactListTest {

    /** Builds a list of name-only contacts, for positional checks. */
    private static ContactList listOf(String... names) {
        ContactList contacts = new ContactList();
        for (String name : names) {
            contacts.add(new Contact(name, "", ""));
        }
        return contacts;
    }

    @Test
    public void size_newList_isZero() {
        assertEquals(0, new ContactList().size());
    }

    @Test
    public void add_severalContacts_keepsTheOrderGiven() throws FF15Exception {
        ContactList contacts = new ContactList();
        contacts.add(new Contact("a", "", ""), new Contact("b", "", ""));
        assertEquals(2, contacts.size());
        assertEquals("a", contacts.get(1).getName());
        assertEquals("b", contacts.get(2).getName());
    }

    @Test
    public void get_firstContact_isNumberOneNotZero() throws FF15Exception {
        assertEquals("a", listOf("a", "b").get(1).getName());
    }

    @Test
    public void get_zeroOrNegative_throwsException() {
        ContactList contacts = listOf("a");
        assertThrows(FF15Exception.class, () -> contacts.get(0));
        assertThrows(FF15Exception.class, () -> contacts.get(-1));
    }

    @Test
    public void get_pastTheEnd_throwsException() {
        ContactList contacts = listOf("a", "b");
        assertThrows(FF15Exception.class, () -> contacts.get(3));
    }

    @Test
    public void get_emptyList_throwsException() {
        ContactList contacts = new ContactList();
        assertThrows(FF15Exception.class, () -> contacts.get(1));
    }

    @Test
    public void delete_middleContact_removesItAndRenumbersTheRest() throws FF15Exception {
        ContactList contacts = listOf("a", "b", "c");
        assertEquals("b", contacts.delete(2).getName());
        assertEquals(2, contacts.size());
        assertEquals("a", contacts.get(1).getName());
        assertEquals("c", contacts.get(2).getName());
    }

    @Test
    public void delete_numberNotInTheList_throwsException() {
        ContactList contacts = listOf("a");
        assertThrows(FF15Exception.class, () -> contacts.delete(2));
    }

    @Test
    public void find_matchingNames_returnsThemInListOrder() {
        ContactList contacts = listOf("John Tan", "Mary Lim", "Johnny Ng");

        List<Contact> matches = contacts.find("john");

        assertEquals(2, matches.size());
        assertEquals("John Tan", matches.get(0).getName());
        assertEquals("Johnny Ng", matches.get(1).getName());
    }

    @Test
    public void find_noMatch_returnsEmptyList() {
        assertTrue(listOf("John").find("mary").isEmpty());
    }

    @Test
    public void find_emptyList_returnsEmptyList() {
        assertTrue(new ContactList().find("john").isEmpty());
    }

    @Test
    public void find_returnedList_isSeparateFromTheContactList() {
        ContactList contacts = listOf("John");
        List<Contact> matches = contacts.find("john");
        matches.clear();
        assertEquals(1, contacts.size());
    }

    @Test
    public void findSame_contactPresent_returnsItsNumber() {
        ContactList contacts = listOf("Ann", "Ben", "Cal");
        assertEquals(2, contacts.findSame(new Contact("Ben", "", "")).getAsInt());
    }

    @Test
    public void findSame_sameNameOtherDetails_returnsEmpty() {
        ContactList contacts = listOf("Ann", "Ben");
        assertTrue(contacts.findSame(new Contact("Ben", "91234567", "")).isEmpty());
    }

    @Test
    public void findSame_twoMatches_returnsTheFirst() {
        ContactList contacts = listOf("Ann", "Ben", "Ben");
        assertEquals(2, contacts.findSame(new Contact("Ben", "", "")).getAsInt());
    }

    @Test
    public void asList_attemptToModify_throwsException() {
        ContactList contacts = listOf("a");
        List<Contact> view = contacts.asList();
        assertThrows(UnsupportedOperationException.class, () -> view.add(new Contact("sneaked in", "", "")));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
    }

    @Test
    public void add_noArguments_tripsTheGuard() {
        assertThrows(AssertionError.class, () -> new ContactList().add());
    }
}
