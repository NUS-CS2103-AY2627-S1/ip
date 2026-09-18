package aether.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aether.exception.AetherException;
import aether.task.Deadline;
import aether.task.Event;
import aether.task.Task;
import aether.task.Todo;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadReturnsEmptyListWhenDataFileDoesNotExist() throws AetherException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoadPreserveEveryTaskTypeStatusDateAndDescription() throws AetherException {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("aether.txt"));
        Todo todo = new Todo("borrow | return library books");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 28));
        Event event = new Event("team meeting", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2));

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][X] borrow | return library books", loadedTasks.get(0).toString());
        assertEquals("[D][ ] submit report (by: Aug 28 2026)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] team meeting (from: Sep 01 2026 to: Sep 02 2026)", loadedTasks.get(2).toString());
        assertEquals(LocalDate.of(2026, 8, 28), assertInstanceOf(Deadline.class, loadedTasks.get(1)).getBy());
        assertEquals(LocalDate.of(2026, 9, 1), assertInstanceOf(Event.class, loadedTasks.get(2)).getFrom());
    }

    @Test
    void loadReportsTheLineContainingCorruptedData() throws IOException {
        Path dataFile = temporaryDirectory.resolve("aether.txt");
        Files.writeString(dataFile, "T | 0 | cmVhZCBib29r\nD | 1 | not-base64 | invalid-date", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        AetherException exception = org.junit.jupiter.api.Assertions.assertThrows(AetherException.class, storage::load);

        assertEquals("Saved task data is corrupted at line 2. Repair or remove data/aether.txt, then restart Aether.",
                exception.getMessage());
    }

    @Test
    void loadRejectsBlankDescriptionsAndInvalidEventRanges() throws IOException {
        Path blankDescriptionFile = temporaryDirectory.resolve("blank.txt");
        Files.writeString(blankDescriptionFile, "T | 0 | IA==", StandardCharsets.UTF_8);
        Storage blankDescriptionStorage = new Storage(blankDescriptionFile);

        AetherException blankException = org.junit.jupiter.api.Assertions.assertThrows(
                AetherException.class, blankDescriptionStorage::load);

        assertTrue(blankException.getMessage().contains("corrupted at line 1"));

        Path invalidEventFile = temporaryDirectory.resolve("event.txt");
        Files.writeString(invalidEventFile,
                "E | 0 | dHJpcA== | MjAyNi0wOS0xNg== | MjAyNi0wOS0xNQ==", StandardCharsets.UTF_8);
        Storage invalidEventStorage = new Storage(invalidEventFile);

        AetherException eventException = org.junit.jupiter.api.Assertions.assertThrows(
                AetherException.class, invalidEventStorage::load);

        assertTrue(eventException.getMessage().contains("corrupted at line 1"));
    }

    @Test
    void loadRejectsMalformedRecordVariants() throws IOException {
        String[] malformedRecords = {
            "T | 0",
            "X | 0 | dGFzaw==",
            "T | 2 | dGFzaw==",
            "T | 0 | not-base64",
            "D | 0 | dGFzaw== | bm90LWEtZGF0ZQ==",
            "D | 0 | dGFzaw== | MjAyNi0wOS0yMA== | extra"
        };

        for (int index = 0; index < malformedRecords.length; index++) {
            Path dataFile = temporaryDirectory.resolve("malformed-" + index + ".txt");
            Files.writeString(dataFile, malformedRecords[index], StandardCharsets.UTF_8);
            Storage storage = new Storage(dataFile);

            AetherException exception = org.junit.jupiter.api.Assertions.assertThrows(
                    AetherException.class, storage::load);

            assertTrue(exception.getMessage().contains("corrupted at line 1"));
        }
    }

    @Test
    void loadAndSaveReportIoFailures() throws IOException {
        Path directoryPath = temporaryDirectory.resolve("not-a-file");
        Files.createDirectory(directoryPath);
        Storage storage = new Storage(directoryPath);

        AetherException loadException = org.junit.jupiter.api.Assertions.assertThrows(
                AetherException.class, storage::load);
        AetherException saveException = org.junit.jupiter.api.Assertions.assertThrows(
                AetherException.class, () -> storage.save(List.of(new Todo("task"))));

        assertTrue(loadException.getMessage().contains("could not read"));
        assertTrue(saveException.getMessage().contains("could not save"));
    }
}
