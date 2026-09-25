package shan;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ShanInitializationTest {
    @TempDir
    Path tempDir;

    @Test
    void initialize_calledTwice_doesNotReloadTasks() throws Exception {
        Path dataFile = tempDir.resolve("data.txt");
        Files.write(dataFile, List.of("T | 0 | first task"));
        Shan shan = new Shan(dataFile);

        assertNull(shan.initialize());
        Files.write(dataFile, List.of("T | 0 | replacement task"));

        assertNull(shan.initialize());
        String response = shan.executeCommand("list").message();
        assertTrue(response.contains("first task"));
        assertFalse(response.contains("replacement task"));
    }

    @Test
    void initialize_multipleInvalidEntries_returnsPluralWarningAndLoadsValidTask() throws Exception {
        Path dataFile = tempDir.resolve("data.txt");
        Files.write(dataFile, List.of(
                "invalid entry",
                "T | invalid status | bad task",
                "T | 0 | valid task"));
        Shan shan = new Shan(dataFile);

        String warning = shan.initialize();

        assertTrue(warning.contains("2 invalid task entries"));
        assertTrue(shan.executeCommand("list").message().contains("valid task"));
    }

    @Test
    void initialize_dataPathIsDirectory_returnsReadWarning() throws Exception {
        Path dataPath = tempDir.resolve("directory");
        Files.createDirectory(dataPath);

        String warning = new Shan(dataPath).initialize();

        assertTrue(warning.contains("I couldn't read"));
        assertTrue(warning.contains("directory"));
    }
}
