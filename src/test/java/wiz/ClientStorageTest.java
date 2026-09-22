package wiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ClientStorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void parseClient_validLineWithoutNote_success() throws IOException {
        ClientStorage storage = new ClientStorage(tempDir.resolve("clients.txt").toString());
        Client client = storage.parseClient("Alice Tan | 91234567 | alice@example.com | ");

        assertEquals("Alice Tan", client.getName());
        assertEquals("91234567", client.getPhone());
        assertEquals("alice@example.com", client.getEmail());
        assertEquals("", client.getNote());
    }

    @Test
    public void parseClient_validLineWithNote_success() throws IOException {
        ClientStorage storage = new ClientStorage(tempDir.resolve("clients.txt").toString());
        Client client = storage.parseClient("Bob Lee | 98765432 | bob@example.com | Life insurance");

        assertEquals("Bob Lee", client.getName());
        assertEquals("98765432", client.getPhone());
        assertEquals("bob@example.com", client.getEmail());
        assertEquals("Life insurance", client.getNote());
    }

    @Test
    public void parseClient_invalidFormat_throwsIOException() {
        ClientStorage storage = new ClientStorage(tempDir.resolve("clients.txt").toString());
        assertThrows(IOException.class, () -> storage.parseClient("Alice Tan | 91234567"));
        assertThrows(IOException.class, () -> storage.parseClient(" | 91234567 | alice@example.com"));
    }

    @Test
    public void saveAndLoad_multipleClients_success() throws IOException {
        Path filePath = tempDir.resolve("clients.txt");
        ClientStorage storage = new ClientStorage(filePath.toString());

        ArrayList<Client> clients = new ArrayList<>();
        clients.add(new Client("Alice Tan", "91234567", "alice@example.com"));
        clients.add(new Client("Bob Lee", "98765432", "bob@example.com", "Life insurance"));

        storage.save(clients);
        ArrayList<Client> loadedClients = storage.load();

        assertEquals(2, loadedClients.size());
        assertEquals(clients.get(0), loadedClients.get(0));
        assertEquals(clients.get(1), loadedClients.get(1));
    }

    @Test
    public void load_nonExistentFile_returnsEmptyList() throws IOException {
        Path filePath = tempDir.resolve("non_existent_clients.txt");
        ClientStorage storage = new ClientStorage(filePath.toString());

        ArrayList<Client> loaded = storage.load();
        assertEquals(0, loaded.size());
    }
}
