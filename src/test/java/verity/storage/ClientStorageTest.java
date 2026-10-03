package verity.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.client.ClientList;
import verity.client.PreferredContactMethod;
import verity.exception.VerityException;
import verity.parser.Parser;

/**
 * Tests client-file loading and saving.
 */
class ClientStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadClientLines_missingFile_returnsEmptyList() throws IOException {
        ClientStorage storage = new ClientStorage(
                temporaryDirectory.resolve("clients.txt"));

        assertEquals(List.of(), storage.loadClientLines());
    }

    @Test
    void saveClients_validClients_writesHeaderAndRecords()
            throws IOException, VerityException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("clients.txt");
        ClientList clients = new ClientList();
        clients.addNewClient(
                "Alice Tan",
                "+65 9123 4567",
                "alice@example.com",
                "",
                "Acme",
                "Called\nRequested quote",
                PreferredContactMethod.EMAIL);

        new ClientStorage(dataFile).saveClients(clients);

        assertEquals(
                List.of(
                        "NEXT_ID\t2",
                        "C\tC001\tAlice Tan\t+65 9123 4567\talice@example.com"
                                + "\t\tAcme\tCalled\\nRequested quote\temail"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void saveAndLoadClients_preservesAllFieldsAndNextId()
            throws IOException, VerityException {
        Path dataFile = temporaryDirectory.resolve("clients.txt");
        ClientList clients = new ClientList();
        clients.addNewClient(
                "Alice Tan", "+65 9123 4567", "alice@example.com",
                "1 Main Street", "Acme", "Called\nRequested quote",
                PreferredContactMethod.EMAIL);
        ClientStorage storage = new ClientStorage(dataFile);

        storage.saveClients(clients);
        ClientList restored = new Parser().parseSavedClients(
                storage.loadClientLines());

        assertEquals(2, restored.getNextId());
        assertEquals(
                clients.getById("C001").serialize(),
                restored.getById("C001").serialize());
    }
}
