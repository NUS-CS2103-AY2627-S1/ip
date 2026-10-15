package koara.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests operations on collections of clients.
 */
public class ClientListTest {

    @Test
    public void clientOperations_addFindUpdateDelete_updatesList() {
        Client alex = new Client("Alex Tan", "91234567", "Run 5 km", "Knee injury");
        Client beth = new Client("Beth Lee", "92345678", "Gain strength", "Vegetarian");
        Client updatedBeth = new Client("Beth Lee", "92345678", "Deadlift 80 kg", "Vegetarian");
        ClientList clients = new ClientList();

        clients.add(alex);
        clients.add(beth);
        assertEquals(2, clients.getSize());
        assertSame(alex, clients.find("knee").get(0));
        assertSame(beth, clients.find("STRENGTH").get(0));

        clients.update(1, updatedBeth);
        assertSame(updatedBeth, clients.get(1));
        assertSame(alex, clients.delete(0));
        assertEquals(List.of("Beth Lee\t92345678\tDeadlift 80 kg\tVegetarian"), clients.toDataLines());
    }

    @Test
    public void constructor_sourceListChanged_doesNotChangeClientList() {
        ArrayList<Client> sourceClients = new ArrayList<>();
        sourceClients.add(new Client("Alex Tan", "91234567", "Run 5 km", "Knee injury"));

        ClientList clients = new ClientList(sourceClients);
        sourceClients.clear();

        assertEquals(1, clients.getSize());
    }

    @Test
    public void containsDuplicate_excludedClientAndOtherIdentity_returnsExpectedResult() {
        Client alex = new Client("Alex Tan", "91234567", "Run", "Healthy");
        Client beth = new Client("Beth Lee", "92345678", "Swim", "Healthy");
        ClientList clients = new ClientList(new ArrayList<>(List.of(alex, beth)));

        assertTrue(clients.containsDuplicate(
                new Client("alex tan", "80000000", "Run", "Healthy"), -1));
        assertTrue(clients.containsDuplicate(
                new Client("Chris", "92345678", "Run", "Healthy"), -1));
        assertFalse(clients.containsDuplicate(
                new Client("Alex Tan", "91234567", "Run faster", "Healthy"), 0));
        assertEquals(0, clients.find("missing").getSize());
    }

    @Test
    public void insertAndInvalidInternalArguments_behaveAsExpected() {
        ClientList clients = new ClientList();
        Client alex = new Client("Alex", "91234567", "Run", "Healthy");
        clients.insert(0, alex);
        assertSame(alex, clients.get(0));

        assertThrows(AssertionError.class, () -> clients.add(null));
        assertThrows(AssertionError.class, () -> clients.insert(2, alex));
        assertThrows(AssertionError.class, () -> clients.insert(0, null));
        assertThrows(AssertionError.class, () -> clients.get(2));
        assertThrows(AssertionError.class, () -> clients.update(2, alex));
        assertThrows(AssertionError.class, () -> clients.update(0, null));
        assertThrows(AssertionError.class, () -> clients.delete(2));
        assertThrows(AssertionError.class, () -> clients.find(""));
        assertThrows(AssertionError.class, () -> clients.containsDuplicate(null, -1));
        assertThrows(AssertionError.class, () -> clients.containsDuplicate(alex, 1));
        assertThrows(AssertionError.class, () -> new ClientList(null));
        assertThrows(AssertionError.class, () -> new ClientList(
                new ArrayList<>(java.util.Arrays.asList((Client) null))));
    }
}
