package ducky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for saving tasks to and loading tasks from storage.
 */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_taggedTasks_taskDetailsRestored() throws DuckyException {
        Path dataFile = temporaryDirectory.resolve("tagged-tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        TaskList originalTasks = new TaskList(List.of(
                new ToDo("Watch movie", List.of("fun", "weekend")),
                new Deadline("Submit report", LocalDate.of(2026, 9, 15), List.of("school")),
                new Event("Team meeting",
                        LocalDateTime.of(2026, 9, 20, 9, 0),
                        LocalDateTime.of(2026, 9, 20, 10, 0),
                        List.of("work"))));

        storage.save(originalTasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals(List.of("fun", "weekend"), loadedTasks.get(0).getTags());
        assertEquals(List.of("school"), loadedTasks.get(1).getTags());
        assertEquals(List.of("work"), loadedTasks.get(2).getTags());
        assertInstanceOf(ToDo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
    }

    @Test
    void load_legacyTask_taskWithoutTagsRestored() throws IOException, DuckyException {
        Path dataFile = temporaryDirectory.resolve("legacy-tasks.txt");
        Files.writeString(dataFile, "T | 1 | Read book\n");
        Storage storage = new Storage(dataFile.toString());

        Task loadedTask = storage.load().get(0);

        assertEquals("Read book", loadedTask.getDescription());
        assertTrue(loadedTask.isDone());
        assertTrue(loadedTask.getTags().isEmpty());
    }

    @Test
    void load_invalidStatus_exceptionThrown() throws IOException {
        Path dataFile = temporaryDirectory.resolve("invalid-status.txt");
        Files.writeString(dataFile, "T | 2 | Read book | -\n");

        assertThrows(DuckyException.class, () -> new Storage(dataFile.toString()).load());
    }

    @Test
    void load_invalidEventRange_exceptionThrown() throws IOException {
        Path dataFile = temporaryDirectory.resolve("invalid-event.txt");
        Files.writeString(dataFile,
                "E | 0 | Meeting | 2026-09-01T10:00 | 2026-09-01T10:00 | -\n");

        assertThrows(DuckyException.class, () -> new Storage(dataFile.toString()).load());
    }

    @Test
    void load_duplicateTasks_exceptionThrown() throws IOException {
        Path dataFile = temporaryDirectory.resolve("duplicate-tasks.txt");
        Files.writeString(dataFile,
                "T | 0 | Read book | -\nT | 1 | Read book | -\n");

        assertThrows(DuckyException.class, () -> new Storage(dataFile.toString()).load());
    }

    @Test
    void load_emptyDescription_exceptionThrown() throws IOException {
        Path dataFile = temporaryDirectory.resolve("empty-description.txt");
        Files.writeString(dataFile, "T | 0 |    | -\n");

        assertThrows(DuckyException.class, () -> new Storage(dataFile.toString()).load());
    }
}
