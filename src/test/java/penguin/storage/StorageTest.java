package penguin.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import penguin.exception.PenguinException;

/**
 * Tests storage file creation, loading, and failure handling.
 */
class StorageTest {
    @Test
    void saveAndLoadTaskLines_validContent_roundTrips(@TempDir Path tempDir) throws PenguinException {
        Storage storage = new Storage(
                tempDir.resolve("data/tasks.txt").toString());
        List<String> content = new ArrayList<>();
        content.add("T | 0 | read book");

        storage.saveTaskLines(content);

        assertEquals(content, storage.loadTaskLines());
    }

    @Test
    void loadTaskLines_missingFile_returnsEmptyList(@TempDir Path tempDir)
            throws PenguinException {
        Storage storage = new Storage(
                tempDir.resolve("missing/tasks.txt").toString());

        assertEquals(new ArrayList<>(), storage.loadTaskLines());
    }

    @Test
    void saveTaskLines_emptyContent_createsEmptyFile(@TempDir Path tempDir)
            throws PenguinException {
        Storage storage = new Storage(
                tempDir.resolve("data/tasks.txt").toString());

        storage.saveTaskLines(new ArrayList<>());

        assertEquals(new ArrayList<>(), storage.loadTaskLines());
    }

    @Test
    void loadTaskLines_directoryPath_throwsPenguinException(@TempDir Path tempDir) {
        Storage storage = new Storage(tempDir.toString());

        assertThrows(PenguinException.class, storage::loadTaskLines);
    }

    @Test
    void saveTaskLines_directoryPath_throwsPenguinException(@TempDir Path tempDir) {
        Storage storage = new Storage(tempDir.toString());

        assertThrows(PenguinException.class,
                () -> storage.saveTaskLines(new ArrayList<>()));
    }

    @Test
    void loadTaskLines_emptyFile_returnsEmptyList(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("data/tasks.txt");
        Files.createDirectories(filePath.getParent());
        Files.createFile(filePath);
        Storage storage = new Storage(filePath.toString());

        assertEquals(new ArrayList<>(), storage.loadTaskLines());
    }
}
