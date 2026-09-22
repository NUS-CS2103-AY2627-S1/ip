package wiz;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class ClientListTest {

    @Test
    public void addAndGet_clientsAdded_correctRetrieval() {
        ClientList clientList = new ClientList();
        Client client1 = new Client("Alice Tan", "91234567", "alice@example.com");
        Client client2 = new Client("Bob Lee", "98765432", "bob@example.com");

        clientList.add(client1, client2);

        assertEquals(2, clientList.size());
        assertEquals(client1, clientList.get(0));
        assertEquals(client2, clientList.get(1));
    }

    @Test
    public void delete_existingClient_clientRemoved() {
        Client client1 = new Client("Alice Tan", "91234567", "alice@example.com");
        Client client2 = new Client("Bob Lee", "98765432", "bob@example.com");
        ClientList clientList = new ClientList(client1, client2);

        Client removed = clientList.delete(0);

        assertEquals(client1, removed);
        assertEquals(1, clientList.size());
        assertEquals(client2, clientList.get(0));
    }

    @Test
    public void find_matchingKeyword_returnsMatchingClients() {
        Client client1 = new Client("Alice Tan", "91234567", "alice@example.com", "Health insurance");
        Client client2 = new Client("Bob Lee", "98765432", "bob@example.com", "Car insurance");
        Client client3 = new Client("Charlie Brown", "93334444", "charlie@example.com", "House mortgage");
        ClientList clientList = new ClientList(client1, client2, client3);

        ArrayList<Client> result = clientList.find("insurance");
        assertEquals(2, result.size());
        assertEquals(client1, result.get(0));
        assertEquals(client2, result.get(1));

        ArrayList<Client> caseInsensitiveResult = clientList.find("INSURANCE");
        assertEquals(2, caseInsensitiveResult.size());
    }

    @Test
    public void hasDuplicate_matchingNameOrPhoneOrEmail_returnsTrue() {
        Client client = new Client("Alice Tan", "91234567", "alice@example.com");
        ClientList clientList = new ClientList(client);

        assertEquals(true, clientList.hasDuplicate(new Client("Alice Tan", "88888888", "other@example.com")));
        assertEquals(true, clientList.hasDuplicate(new Client("Other", "91234567", "other@example.com")));
        assertEquals(true, clientList.hasDuplicate(new Client("Other", "88888888", "alice@example.com")));
        assertEquals(false, clientList.hasDuplicate(new Client("Dave", "88888888", "dave@example.com")));
    }
}
