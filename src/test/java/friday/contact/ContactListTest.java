package friday.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests unique names, numbering, search, and snapshot isolation through contact operations. */
class ContactListTest {
    @Test
    void add_duplicateName_rejectsWithoutChangingList() {
        Contact alice = new Contact("Alice", "91234567", "");
        ContactList contacts = new ContactList(List.of(alice));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> contacts.add(new Contact(" ALICE ", "", "other@example.com")));
        assertEquals("A contact with this name already exists.", error.getMessage());
        assertEquals(List.of(alice), contacts.toList());
        assertThrows(IllegalArgumentException.class, () -> new ContactList(List.of(alice, alice)));
    }

    @Test
    void add_distinctNamesWithSharedDetails_preservesInsertionOrder() {
        ContactList contacts = new ContactList();
        contacts.add(new Contact("Alice Tan", "91234567", "a@b.c"));
        contacts.add(new Contact("Alice  Tan", "91234567", "a@b.c"));
        contacts.add(new Contact("Bob", "91234567", "a@b.c"));
        assertEquals(List.of("Alice Tan", "Alice  Tan", "Bob"),
                contacts.toList().stream().map(Contact::getName).toList());
    }

    @Test
    void delete_middleThenEdges_renumbersAndAllowsNameReuse() {
        Contact alice = new Contact("Alice", "91234567", "");
        Contact bob = new Contact("Bob", "81234567", "");
        Contact carol = new Contact("Carol", "61234567", "");
        ContactList contacts = new ContactList(List.of(alice, bob, carol));
        assertEquals(bob, contacts.delete(2));
        assertEquals(carol, contacts.get(2));
        contacts.add(bob);
        assertEquals(bob, contacts.get(3));
        assertEquals(alice, contacts.delete(1));
        assertEquals(bob, contacts.delete(2));
        assertEquals(carol, contacts.delete(1));
        assertEquals(0, contacts.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE})
    void delete_invalidNumber_preservesContents(int number) {
        Contact alice = new Contact("Alice", "91234567", "");
        ContactList contacts = new ContactList(List.of(alice));
        assertEquals("The contact number is invalid.", assertThrows(IllegalArgumentException.class,
                () -> contacts.delete(number)).getMessage());
        assertThrows(IllegalArgumentException.class, () -> contacts.get(number));
        assertEquals(List.of(alice), contacts.toList());
    }

    @Test
    void findContactNumbersContaining_allFields_preservesOriginalNumbersAndState() {
        ContactList contacts = new ContactList(List.of(new Contact("Alice", "91234567", ""),
                new Contact("Bob", "", "bob@Example.com"), new Contact("Carol", "81234567", "c@example.com")));
        List<Contact> before = contacts.toList();
        assertEquals(List.of(2, 3), contacts.findContactNumbersContaining(" EXAMPLE "));
        assertEquals(List.of(1, 3), contacts.findContactNumbersContaining("1234567"));
        assertEquals(List.of(1), contacts.findContactNumbersContaining("ALICE"));
        assertEquals(List.of(), contacts.findContactNumbersContaining("missing"));
        assertThrows(IllegalArgumentException.class, () -> contacts.findContactNumbersContaining("  "));
        assertEquals(before, contacts.toList());
    }

    @Test
    void toList_sourceAndLaterMutations_doNotAffectSnapshots() {
        Contact alice = new Contact("Alice", "91234567", "");
        List<Contact> source = new ArrayList<>(List.of(alice));
        ContactList contacts = new ContactList(source);
        source.clear();
        List<Contact> snapshot = contacts.toList();
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        contacts.delete(1);
        assertEquals(List.of(alice), snapshot);
        assertThrows(IllegalArgumentException.class, () -> contacts.delete(1));
    }
}
