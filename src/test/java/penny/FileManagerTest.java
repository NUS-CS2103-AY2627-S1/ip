package penny;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
    void readData_missingFile_createsEmptyFileAndParents() {
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
    void fileOperations_parentIsFile_reportFailureWithoutThrowing() throws Exception {
        Path parent = directory.resolve("file.txt");
        Files.writeString(parent, "preserve");
        Path invalid = parent.resolve("commands.txt");
        PrintStream original = System.err;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setErr(captured);
            FileManager.writeData(invalid.toString(), "data");
            assertEquals("", FileManager.readData(invalid.toString()));
        } finally {
            System.setErr(original);
        }
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Failed to write to file:"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Failed to read file:"));
        assertEquals("preserve", Files.readString(parent));
    }
}
