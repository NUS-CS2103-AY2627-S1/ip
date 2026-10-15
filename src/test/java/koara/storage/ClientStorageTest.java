package koara.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import koara.client.Client;
import koara.client.ClientList;
import koara.exception.KoaraException;

/**
 * Tests loading and saving client records.
 */
public class ClientStorageTest {

    @TempDir
    Path tempDirectory;

    @Test
    public void saveAndLoad_clients_preservesAllDetails() throws KoaraException {
        Path filePath = tempDirectory.resolve("data").resolve("clients.txt");
        ClientStorage storage = new ClientStorage(filePath);
        ClientList clients = new ClientList();
        clients.add(new Client("Alex Tan", "91234567", "Run 5 km", "Knee injury"));
        clients.add(new Client("Beth Lee", "92345678", "Gain strength", "Vegetarian"));

        storage.save(clients);
        ClientList loadedClients = storage.load();

        assertTrue(Files.exists(filePath));
        assertEquals(List.of(
                "Alex Tan\t91234567\tRun 5 km\tKnee injury",
                "Beth Lee\t92345678\tGain strength\tVegetarian"
        ), loadedClients.toDataLines());
    }

    @Test
    public void load_missingFile_returnsEmptyClientList() throws KoaraException {
        ClientStorage storage = new ClientStorage(tempDirectory.resolve("missing.txt"));

        assertEquals(0, storage.load().getSize());
    }

    @Test
    public void load_invalidClientData_throwsKoaraException() throws IOException {
        Path filePath = tempDirectory.resolve("clients.txt");
        Files.writeString(filePath, "Alex Tan\t91234567\t\tKnee injury");
        ClientStorage storage = new ClientStorage(filePath);

        assertThrows(KoaraException.class, storage::load);
    }

    @Test
    public void load_invalidPhone_throwsKoaraException() throws IOException {
        Path filePath = tempDirectory.resolve("clients.txt");
        Files.writeString(filePath, "Alex Tan\t1 2 3 4\tRun 5 km\tHealthy");
        ClientStorage storage = new ClientStorage(filePath);

        assertThrows(KoaraException.class, storage::load);
    }

    @Test
    public void load_duplicateClientIdentity_throwsKoaraException() throws IOException {
        Path filePath = tempDirectory.resolve("clients.txt");
        Files.writeString(filePath, "Alex Tan\t91234567\tRun\tHealthy\n"
                + "Beth Lee\t91234567\tSwim\tHealthy");
        ClientStorage storage = new ClientStorage(filePath);

        assertThrows(KoaraException.class, storage::load);
    }

    @Test
    public void save_pathIsDirectory_throwsKoaraException() throws IOException {
        Path directoryPath = Files.createDirectory(tempDirectory.resolve("clients.txt"));
        ClientStorage storage = new ClientStorage(directoryPath);

        assertThrows(KoaraException.class, () -> storage.save(new ClientList()));
    }

    @Test
    public void load_invalidFieldCountsAndDuplicateName_throwKoaraException() throws IOException {
        Path filePath = tempDirectory.resolve("clients.txt");
        ClientStorage storage = new ClientStorage(filePath);

        Files.writeString(filePath, "Alex\t91234567\tRun");
        assertThrows(KoaraException.class, storage::load);
        Files.writeString(filePath, "Alex\t91234567\tRun\tHealthy\textra");
        assertThrows(KoaraException.class, storage::load);
        Files.writeString(filePath, "Alex\t91234567\tRun\tHealthy\n"
                + "alex\t92345678\tSwim\tHealthy");
        assertThrows(KoaraException.class, storage::load);
    }

    @Test
    public void save_replacesExistingFileAndRemovesTemporaryFile() throws Exception {
        Path filePath = tempDirectory.resolve("data").resolve("clients.txt");
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, "old data");
        ClientStorage storage = new ClientStorage(filePath);
        ClientList clients = new ClientList();
        clients.add(new Client("Alex", "91234567", "Run", "Healthy"));

        storage.save(clients);

        assertEquals(List.of("Alex\t91234567\tRun\tHealthy"), Files.readAllLines(filePath));
        try (Stream<Path> savedFiles = Files.list(filePath.getParent())) {
            assertEquals(1, savedFiles.count());
        }
    }

    @Test
    public void constructor_nullPath_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new ClientStorage(null));
    }
}
