package verity.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import verity.exception.VerityException;

/**
 * Tests client collection operations and uniqueness rules.
 */
class ClientListTest {
    @Test
    void addNewClient_multipleClients_generatesSequentialIds()
            throws VerityException {
        ClientList clients = new ClientList();

        Client first = addClient(clients, "Bob Lee", "123 456", "bob@example.com");
        Client second = addClient(clients, "Alice Tan", "987 654", "alice@example.com");

        assertEquals("C001", first.getId());
        assertEquals("C002", second.getId());
        assertEquals(List.of(second, first), clients.getSortedClients());
    }

    @Test
    void addNewClient_duplicateNormalizedPhone_throwsVerityException()
            throws VerityException {
        ClientList clients = new ClientList();
        addClient(clients, "Alice Tan", "+65 9123-4567", "");

        assertThrows(
                VerityException.class,
                () -> addClient(clients, "Bob Lee", "+65(9123)4567", ""));
    }

    @Test
    void addNewClient_duplicateCaseInsensitiveEmail_throwsVerityException()
            throws VerityException {
        ClientList clients = new ClientList();
        addClient(clients, "Alice Tan", "", "Alice@example.com");

        assertThrows(
                VerityException.class,
                () -> addClient(clients, "Bob Lee", "", "alice@example.com"));
    }

    @Test
    void delete_highestId_nextIdIsNotReused() throws VerityException {
        ClientList clients = new ClientList();
        addClient(clients, "Alice Tan", "", "");
        Client deleted = addClient(clients, "Bob Lee", "", "");

        clients.delete(deleted.getId());
        Client replacement = addClient(clients, "Carol Lim", "", "");

        assertEquals("C003", replacement.getId());
    }

    @Test
    void findByName_partialMixedCase_returnsSortedMatches()
            throws VerityException {
        ClientList clients = new ClientList();
        Client second = addClient(clients, "Malice Ong", "", "");
        Client first = addClient(clients, "Alice Tan", "", "");

        assertEquals(List.of(first, second), clients.findByName("ALI"));
    }

    @Test
    void replace_existingClient_updatesFields() throws VerityException {
        ClientList clients = new ClientList();
        addClient(clients, "Alice Tan", "123", "alice@example.com");
        Client replacement = new Client(
                1, "Alice Lim", "456", "lim@example.com", "", "", "", null);

        clients.replace(replacement);

        assertEquals(replacement, clients.getById("C001"));
    }

    @Test
    void replaceOrGet_missingClient_throwsVerityException() throws VerityException {
        ClientList clients = new ClientList();
        Client missing = new Client(
                2, "Bob Lee", "", "", "", "", "", null);

        assertThrows(VerityException.class, () -> clients.replace(missing));
        assertThrows(VerityException.class, () -> clients.getById("C002"));
    }

    @Test
    void containsId_validInvalidAndMissingIds_returnsExpectedResult()
            throws VerityException {
        ClientList clients = new ClientList();
        addClient(clients, "Alice Tan", "", "");

        assertTrue(clients.containsId("c001"));
        assertFalse(clients.containsId("C002"));
        assertFalse(clients.containsId("invalid"));
    }

    @Test
    void constructor_duplicateLoadedId_throwsVerityException()
            throws VerityException {
        Client client = new Client(
                1, "Alice Tan", "", "", "", "", "", null);

        VerityException exception = assertThrows(
                VerityException.class,
                () -> new ClientList(2, List.of(client, client)));

        assertEquals("duplicate client ID 'C001'.", exception.getMessage());
    }

    @Test
    void constructor_invalidNextId_throwsVerityException()
            throws VerityException {
        Client client = new Client(
                2, "Alice Tan", "", "", "", "", "", null);

        assertThrows(
                VerityException.class,
                () -> new ClientList(2, List.of(client)));
        assertThrows(
                VerityException.class,
                () -> new ClientList(0, List.of()));
    }

    private Client addClient(ClientList clients, String name, String phone,
            String email) throws VerityException {
        return clients.addNewClient(
                name, phone, email, "", "", "", null);
    }
}
