package penny;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies file persistence using disposable paths, including deterministic I/O failures.
 */
public class FileManagerTest {
    @TempDir
    Path directory;

    @Test
    void readData_missingFile_createsEmptyFileAndParents() throws PennyException {
        Path file = directory.resolve("nested/commands.txt");
        assertEquals("", FileManager.readData(file.toString()));
        assertTrue(Files.isRegularFile(file));
    }

    @Test
    void writeData_existingFile_replacesContents() throws Exception {
        Path file = directory.resolve("nested/commands.txt");
        String content = "todo café\nイベント\n";
        FileManager.writeData(file.toString(), content);
        assertEquals(content, Files.readString(file));
        assertEquals(content, FileManager.readData(file.toString()));
        FileManager.writeData(file.toString(), "short");
        assertEquals("short", FileManager.readData(file.toString()));
        FileManager.writeData(file.toString(), "");
        assertEquals("", FileManager.readData(file.toString()));
    }

    @Test
    void fileOperations_parentIsFile_reportFailure() throws Exception {
        Path parent = directory.resolve("file.txt");
        Files.writeString(parent, "preserve");
        Path invalid = parent.resolve("commands.txt");
        assertTrue(assertThrows(PennyException.class, () -> FileManager.writeData(invalid.toString(), "data"))
                .getMessage().contains("Could not save tasks"));
        assertTrue(assertThrows(PennyException.class, () -> FileManager.readData(invalid.toString()))
                .getMessage().contains("Could not read tasks"));
        assertEquals("preserve", Files.readString(parent));
    }
}
