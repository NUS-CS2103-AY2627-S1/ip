package oreo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import oreo.exception.OreoException;

/** Tests task-data file creation, replacement, and validation. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void constructor_missingNestedFile_createsParentsAndEmptyFile() {
        Path file = temporaryDirectory.resolve("nested").resolve("data").resolve("Oreo.txt");

        Storage storage = new Storage(file.toString());

        assertEquals("", storage.load());
        assertTrue(Files.isRegularFile(file));
    }

    @Test
    void save_replacesPreviousContentAndPreservesUnicode() {
        Path file = temporaryDirectory.resolve("Oreo.txt");
        Storage storage = new Storage(file.toString());

        storage.save("first");
        storage.save("café \u2615");

        assertEquals("café \u2615", storage.load());
    }

    @Test
    void constructor_directoryInsteadOfFile_throwsHelpfulException() throws Exception {
        Path directory = temporaryDirectory.resolve("not-a-file");
        Files.createDirectory(directory);

        assertThrows(OreoException.class, () -> new Storage(directory.toString()));
    }
}
