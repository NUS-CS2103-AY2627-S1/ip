package bos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests conversion between tasks and their text-file storage records.
 */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveTasks_allTaskTypes_expectedStorageRecords() throws IOException {
        Task todo = new TodoTask("read book");
        todo.markAsDone();
        Task deadline = new Deadline("return book", "2026-09-12 1800");
        Task event = new Event("project meeting", "2026-09-13 1000", "2026-09-13 1100");
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(taskFile.toString());

        storage.saveTasks(List.of(todo, deadline, event));

        assertIterableEquals(
                List.of(
                        "T | 1 | read book",
                        "D | 0 | return book | 2026-09-12 1800",
                        "E | 0 | project meeting | 2026-09-13 1000 | 2026-09-13 1100"),
                Files.readAllLines(taskFile));
    }

    @Test
    public void loadTasks_validRecords_equivalentTasksReturned() throws IOException, BosException {
        List<String> storedRecords = List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2026-09-12 1800",
                "E | 0 | project meeting | 2026-09-13 1000 | 2026-09-13 1100");
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(taskFile, storedRecords);
        Storage storage = new Storage(taskFile.toString());

        List<String> reloadedRecords = storage.loadTasks().stream()
                .map(Task::formatForStorage)
                .toList();

        assertIterableEquals(storedRecords, reloadedRecords);
    }

    @Test
    public void loadTasks_malformedRecords_exceptionThrown() throws IOException {
        List<String> malformedRecords = List.of(
                "",
                "T | 2 | read book",
                "X | 0 | read book",
                "T | 0 |",
                "T | 0 | read book | unexpected",
                "D | 0 | report | Feb 30",
                "D | 0 | report | 2026-02-30 1200",
                "E | 0 | meeting | 2026-09-11 1100 | 2026-09-11 1000");
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(taskFile.toString());

        for (String malformedRecord : malformedRecords) {
            Files.writeString(taskFile, malformedRecord + System.lineSeparator());

            assertThrows(BosException.class, storage::loadTasks, malformedRecord);
        }
    }

    @Test
    public void loadTasks_duplicateDescriptions_exceptionThrown() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(
                taskFile,
                List.of(
                        "T | 0 | Read Book",
                        "D | 0 | read book | 2026-09-18 1800"));
        Storage storage = new Storage(taskFile.toString());

        BosException exception = assertThrows(BosException.class, storage::loadTasks);

        assertTrue(exception.getMessage().contains("line 2"));
        assertTrue(exception.getMessage().contains("duplicate task description"));
    }

    @Test
    public void saveTasks_parentDirectoryMissing_directoryAndFileCreated() throws IOException {
        Path taskFile = temporaryDirectory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(taskFile.toString());

        storage.saveTasks(List.of(new TodoTask("read book")));

        assertTrue(Files.isRegularFile(taskFile));
        assertEquals("T | 0 | read book" + System.lineSeparator(), Files.readString(taskFile));
    }

    @Test
    public void loadTasks_storagePathIsDirectory_exceptionThrown() {
        Storage storage = new Storage(temporaryDirectory.toString());

        assertThrows(IOException.class, storage::loadTasks);
    }
}
