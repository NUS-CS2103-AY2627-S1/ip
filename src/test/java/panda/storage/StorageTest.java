package panda.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Contains unit tests for decoding complete task save files.
 */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void readTasks_overlappingStoredEvents_returnsDecodedTasks() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(saveFile, List.of(
                "E | 0 | lecture | 2026-09-10 09:00 | 2026-09-10 11:00",
                "E | 1 | workshop | 2026-09-10 10:00 | 2026-09-10 12:00"),
                StandardCharsets.UTF_8);

        assertEquals(2, Storage.readTasks(saveFile).size());
    }

    @Test
    public void readTasks_adjacentStoredEvents_returnsBothTasks() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(saveFile, List.of(
                "E | 0 | lecture | 2026-09-10 09:00 | 2026-09-10 10:00",
                "E | 1 | workshop | 2026-09-10 10:00 | 2026-09-10 12:00"),
                StandardCharsets.UTF_8);

        assertEquals(2, Storage.readTasks(saveFile).size());
    }
}
