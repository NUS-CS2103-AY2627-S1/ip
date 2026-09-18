package plana.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import plana.client.Client;
import plana.client.ClientList;

/**
 * Tests client persistence, escaping, malformed records, and duplicate handling.
 */
class ClientStorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void saveAndLoadClients_roundTripPreservesFields() throws Exception {
        Client client = new Client("Alice | Tan", "ALICE@example.com", "91234567",
                "12 Baker Street", "no nuts", "birthday cake");
        ClientList clients = new ClientList(List.of(client));
        ClientStorage storage = new ClientStorage(temporaryDirectory.resolve("clients.txt").toString());

        storage.saveClients(clients);
        Client loadedClient = storage.loadClients().get(0);

        assertEquals(client.toStorageString(), loadedClient.toStorageString());
        assertEquals("alice@example.com", loadedClient.getEmail());
    }

    @Test
    void loadClients_malformedAndDuplicateRecordsValidRecordsPreserved() throws IOException {
        Path dataFile = temporaryDirectory.resolve("clients.txt");
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "C | alice@example.com | Alice | 91234567 |  |  | ",
                "C | ALICE@example.com | Duplicate | 91234567 |  |  | ",
                "C | invalid | Missing valid domain |  |  |  | ",
                "not a client record",
                "C | bob@example.com | Bob |  |  | vegan | "));

        ClientList clients = new ClientStorage(dataFile.toString()).loadClients();

        assertEquals(2, clients.size());
        assertEquals("alice@example.com", clients.get(0).getEmail());
        assertEquals("bob@example.com", clients.get(1).getEmail());
    }

    @Test
    void loadClients_missingFile_emptyListReturned() {
        ClientList clients = new ClientStorage(temporaryDirectory.resolve("missing.txt").toString()).loadClients();

        assertTrue(clients.size() == 0);
    }

    @Test
    void saveClients_nullAndEmptyLists_emptyFileAndListReturned() throws IOException {
        Path dataFile = temporaryDirectory.resolve("empty.txt");
        ClientStorage storage = new ClientStorage(dataFile.toString());

        assertTrue(storage.saveClients(null));
        assertEquals("", Files.readString(dataFile));
        assertEquals(0, storage.loadClients().size());
        assertTrue(storage.saveClients(new ClientList()));
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    void clientStoragePath_directorySaveFailsAndLoadIsEmpty() throws IOException {
        Path directoryPath = temporaryDirectory.resolve("directory-target");
        Files.createDirectory(directoryPath);
        ClientStorage storage = new ClientStorage(directoryPath.toString());

        assertFalse(storage.saveClients(new ClientList(List.of(
                new Client("Alice", "alice@example.com", "", "", "", "")))));
        assertEquals(0, storage.loadClients().size());
    }

    @Test
    void loadClients_invalidFieldsAndEscapes_areSkipped() throws IOException {
        Path dataFile = temporaryDirectory.resolve("invalid-clients.txt");
        String longName = "a".repeat(101);
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "",
                "C | missing@example.com | \\ |  |  |  | ",
                "C | invalid | Invalid email |  |  |  | ",
                "C | bad@example.com | Bad phone | 123 |  |  | ",
                "C | control@example.com | Bad notes |  |  |  | bad\\nnotes",
                "C | long@example.com | " + longName + " |  |  |  | ",
                "C | good@example.com | Good | +65 9123-4567 |  | | ",
                "C | escape@example.com | Bad\\q |  |  |  | ",
                "C | trailing@example.com | Bad\\"));

        ClientList clients = new ClientStorage(dataFile.toString()).loadClients();

        assertEquals(1, clients.size());
        assertEquals("good@example.com", clients.get(0).getEmail());
        assertEquals("+65 9123-4567", clients.get(0).getPhone());
    }
}
