package atlas.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the client list operations. */
public class ClientListTest {

    /** Client used in several tests. */
    private static final Client BOB = new Client("Bob", "91234567", "");
    /** Client used in several tests. */
    private static final Client MARY = new Client("Mary Jane", "", "mary@x.com");
    /** Client used in several tests. */
    private static final Client CHEN = new Client("Chen Wei", "8123", "wei@example.com");

    /**
     * Returns a list holding the three test clients, in insertion order.
     *
     * @return list holding Bob, Mary Jane and Chen Wei.
     */
    private ClientList threeClients() {
        return new ClientList(new ArrayList<>(List.of(BOB, MARY, CHEN)));
    }

    @Test
    void emptyListReportsEmptyAndSizeZero() {
        ClientList clients = new ClientList();

        assertTrue(clients.isEmpty());
        assertEquals(0, clients.size());
        assertTrue(clients.all().isEmpty());
    }

    @Test
    void addsAndGetsClientsInInsertionOrder() {
        ClientList clients = new ClientList();

        clients.add(BOB);
        clients.add(MARY);

        assertFalse(clients.isEmpty());
        assertEquals(2, clients.size());
        assertEquals("Bob", clients.get(0).getName());
        assertEquals("Mary Jane", clients.get(1).getName());
    }

    @Test
    void removesClientAndShiftsTheRemainingOnes() {
        ClientList clients = threeClients();

        Client removed = clients.remove(1);

        assertEquals("Mary Jane", removed.getName());
        assertEquals(2, clients.size());
        assertEquals("Bob", clients.get(0).getName());
        assertEquals("Chen Wei", clients.get(1).getName());
    }

    @Test
    void findsClientsByKeywordInAnyField() {
        ClientList clients = threeClients();

        assertEquals(1, clients.find("Bob").size());
        assertEquals(1, clients.find("9123").size());
        assertEquals(1, clients.find("wei@example.com").size());
        assertEquals(2, clients.find("Mary", "Chen").size());
        assertEquals(0, clients.find("nobody").size());
    }

    @Test
    void findPreservesInsertionOrderRatherThanAlphabeticalOrder() {
        ClientList clients = new ClientList(new ArrayList<>(List.of(
                new Client("Zara", "9000 0001", ""),
                new Client("Adam", "9000 0002", ""),
                new Client("Mary", "9000 0003", ""))));

        ArrayList<Client> matches = clients.find("9000");

        assertEquals(3, matches.size());
        assertEquals("Zara", matches.get(0).getName());
        assertEquals("Adam", matches.get(1).getName());
        assertEquals("Mary", matches.get(2).getName());
    }

    @Test
    void findIsCaseSensitiveLikeTaskSearch() {
        ClientList clients = threeClients();

        assertEquals(0, clients.find("bob").size());
        assertEquals(0, clients.find("WEI@EXAMPLE.COM").size());
        assertEquals(0, clients.find("mary jane").size());
        assertEquals(1, clients.find("Bob").size());
    }
}
