package plana.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import plana.exception.PlanaException;

/**
 * Tests client ordering, references, uniqueness, and replacement.
 */
class ClientListTest {
    private final Client alice = new Client("Alice", "alice@example.com", "", "", "", "");
    private final Client bob = new Client("Bob", "bob@example.com", "", "", "", "");

    @Test
    void add_duplicateEmailRejectedCaseInsensitively() throws PlanaException {
        ClientList clients = new ClientList();
        clients.add(alice);

        PlanaException exception = assertThrows(PlanaException.class, () ->
                clients.add(new Client("Other", "ALICE@example.com", "", "", "", "")));

        assertEquals("Oops, a client with email 'alice@example.com' already exists.", exception.getMessage());
    }

    @Test
    void referencesAndDeletion_followCurrentListPositions() throws PlanaException {
        ClientList clients = new ClientList(List.of(alice, bob));

        assertEquals(alice, clients.get("C1", "view"));
        assertEquals(bob, clients.delete("C2"));
        assertEquals(1, clients.size());
    }

    @Test
    void matchingIndexes_preserveFullListPositions() {
        ClientList clients = new ClientList(List.of(alice, bob));

        assertEquals(List.of(1), clients.matchingIndexes("bob"));
    }

    @Test
    void addAndReplace_validClientsPreserveOrderAndAllowSameEmail() throws PlanaException {
        ClientList clients = new ClientList();
        clients.add(alice);
        clients.add(bob);
        Client replacement = new Client("Alicia", "ALICE@example.com", "", "", "", "");

        clients.replace("C1", replacement);

        assertSame(replacement, clients.get("C1", "view"));
        assertSame(bob, clients.get("C2", "view"));
    }

    @Test
    void replace_duplicateEmailRejectedAndOriginalClientPreserved() throws PlanaException {
        ClientList clients = new ClientList(List.of(alice, bob));
        Client replacement = new Client("Alicia", "BOB@example.com", "", "", "", "");

        PlanaException exception = assertThrows(PlanaException.class, () ->
                clients.replace("C1", replacement));

        assertEquals("Oops, a client with email 'bob@example.com' already exists.", exception.getMessage());
        assertSame(alice, clients.get(0));
    }

    @Test
    void snapshotsRestoreAndIteration_preserveClientReferencesAndOrder() throws PlanaException {
        ClientList clients = new ClientList(List.of(alice, bob));
        List<Client> snapshot = clients.copyClients();
        ArrayList<Client> iteratedClients = new ArrayList<>();
        Iterator<Client> iterator = clients.iterator();

        clients.delete("C1");
        clients.restoreClients(snapshot);
        iterator = clients.iterator();
        iterator.forEachRemaining(iteratedClients::add);

        assertEquals(List.of(alice, bob), snapshot);
        assertEquals(List.of(alice, bob), iteratedClients);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(alice));
    }

    @Test
    void invalidReferences_returnActionableErrors() {
        ClientList clients = new ClientList(List.of(alice));

        PlanaException missingReference = assertThrows(PlanaException.class, () ->
                clients.get(null, "view"));
        PlanaException malformedReference = assertThrows(PlanaException.class, () ->
                clients.get("C01", "view"));
        PlanaException tooLargeReference = assertThrows(PlanaException.class, () ->
                clients.get("C999999999999999999999", "view"));
        PlanaException outOfRangeReference = assertThrows(PlanaException.class, () ->
                clients.delete("C2"));

        assertEquals("Oops, client view needs a client position. Try: client view C1.",
                missingReference.getMessage());
        assertEquals("Oops, 'C01' isn't a valid client position. Use a reference like C1.",
                malformedReference.getMessage());
        assertEquals("Oops, 'C999999999999999999999' isn't a valid client position."
                + " Use a reference like C1.", tooLargeReference.getMessage());
        assertEquals("Oops, client C2 doesn't exist yet."
                + " Type client list to check the client positions you have.", outOfRangeReference.getMessage());
    }

    @Test
    void nullClientsAndSnapshots_assertionsFail() {
        ClientList clients = new ClientList();

        assertThrows(AssertionError.class, () -> new ClientList((List<Client>) null));
        assertThrows(AssertionError.class, () -> clients.add(null));
        assertThrows(AssertionError.class, () -> clients.restoreClients(null));
        assertTrue(clients.size() == 0);
        assertFalse(clients.matchingIndexes("alice").contains(0));
    }
}
