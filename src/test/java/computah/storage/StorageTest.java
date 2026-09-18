package computah.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import computah.client.Client;
import computah.exception.ComputahException;

/**
 * Tests client persistence behavior in {@link Storage}.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveAndLoadClients_validClients_preservesClientData() throws ComputahException {
        Path clientFile = temporaryDirectory.resolve("clients.txt");
        Storage storage = createStorage(clientFile);
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(new Client("Alice Tan", "+65 9123 4567", "alice@example.com"));
        clients.add(new Client("Bob Lee", "", "bob@example.com"));

        storage.saveClients(clients);
        ArrayList<Client> loadedClients = storage.loadClients();

        assertEquals(2, loadedClients.size());
        assertEquals(clients.get(0).toFileString(), loadedClients.get(0).toFileString());
        assertEquals(clients.get(1).toFileString(), loadedClients.get(1).toFileString());
    }

    @Test
    public void loadClients_missingFile_returnsEmptyList() throws ComputahException {
        Storage storage = createStorage(temporaryDirectory.resolve("missing.txt"));

        assertEquals(0, storage.loadClients().size());
    }

    @Test
    public void loadClients_malformedRecord_exceptionThrown() throws IOException {
        Path clientFile = temporaryDirectory.resolve("clients.txt");
        Files.writeString(clientFile, "C | Alice Tan | 123\n");
        Storage storage = createStorage(clientFile);

        ComputahException exception = assertThrows(ComputahException.class, storage::loadClients);

        assertEquals("I could not load the client list.", exception.getMessage());
    }

    private Storage createStorage(Path clientFile) {
        return new Storage(temporaryDirectory.resolve("duke.txt").toString(), clientFile.toString());
    }
}
