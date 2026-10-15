package orbit.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import orbit.exception.OrbitException;
import orbit.task.Deadline;
import orbit.task.Event;
import orbit.task.Task;
import orbit.task.Todo;

/**
 * Tests saving and loading task data through {@link Storage}.
 */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that loading from a missing file starts with an empty task list.
     *
     * @throws IOException if storage access unexpectedly fails
     * @throws OrbitException if valid storage data is unexpectedly rejected
     */
    @Test
    void loadTasks_missingFile_returnsEmptyList() throws IOException, OrbitException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/tasks.txt").toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    /**
     * Verifies that all task types and completion states survive a save-load round trip.
     *
     * @throws IOException if storage access unexpectedly fails
     * @throws OrbitException if the saved test data is unexpectedly rejected
     */
    @Test
    void saveAndLoadTasks_allTaskTypesAndStatuses_roundTripsData() throws IOException, OrbitException {
        Path taskFile = temporaryDirectory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(taskFile.toString());
        Todo todo = new Todo("read book");
        todo.mark();
        List<Task> originalTasks = List.of(
                todo,
                new Deadline("return book", LocalDate.of(2019, 12, 2)),
                new Event("project meeting", LocalDate.of(2019, 12, 3), LocalDate.of(2019, 12, 4)));

        storage.saveTasks(originalTasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertTrue(Files.exists(taskFile));
        assertEquals(originalTasks.size(), loadedTasks.size());
        assertEquals("T | 1 | read book", loadedTasks.get(0).toDataString());
        assertEquals("D | 0 | return book | 2019-12-02", loadedTasks.get(1).toDataString());
        assertEquals("E | 0 | project meeting | 2019-12-03 | 2019-12-04",
                loadedTasks.get(2).toDataString());
    }

    /**
     * Verifies that blank lines in a storage file do not create tasks or prevent loading.
     *
     * @throws IOException if storage access unexpectedly fails
     * @throws OrbitException if the valid task entry is unexpectedly rejected
     */
    @Test
    void loadTasks_blankLines_ignoresBlankEntries() throws IOException, OrbitException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "\nT | 0 | read book\n   \n", StandardCharsets.UTF_8);

        ArrayList<Task> tasks = new Storage(taskFile.toString()).loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    /**
     * Verifies that malformed task types, statuses, fields, and dates are rejected.
     *
     * @throws IOException if a malformed temporary file cannot be prepared
     */
    @Test
    void loadTasks_invalidSavedData_throwsOrbitException() throws IOException {
        assertInvalidSavedLine("unknown type", "X | 0 | read book");
        assertInvalidSavedLine("invalid status", "T | 2 | read book");
        assertInvalidSavedLine("missing field", "D | 0 | return book");
        assertInvalidSavedLine("invalid date", "D | 0 | return book | 2019-02-30");
        assertInvalidSavedLine("reversed event dates", "E | 0 | camp | 2019-12-04 | 2019-12-03");
        assertInvalidSavedLine("too many fields", "T | 0 | read book | extra");
    }

    /**
     * Writes one malformed storage line and verifies that loading rejects it.
     *
     * @param fileName unique file name for the malformed example
     * @param taskLine malformed task data
     * @throws IOException if the temporary test file cannot be written
     */
    private void assertInvalidSavedLine(String fileName, String taskLine) throws IOException {
        Path taskFile = temporaryDirectory.resolve(fileName + ".txt");
        Files.writeString(taskFile, taskLine, StandardCharsets.UTF_8);

        assertThrows(OrbitException.class, () -> new Storage(taskFile.toString()).loadTasks());
    }
}
