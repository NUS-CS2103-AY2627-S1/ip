package grower.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import grower.exceptions.GrowerException;
import grower.tasks.Deadline;
import grower.tasks.Event;
import grower.tasks.Task;
import grower.tasks.ToDo;

/**
 * Verifies saved task validation and safe file replacement.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void parseTask_validTodo_restoresDescriptionAndCompletionState() throws GrowerException {
        Storage storage = createStorage();
        Task incompleteTask = storage.parseTask("T | 0 | read book");
        Task completedTask = storage.parseTask("T | 1 | submit work");

        assertAll(
                () -> assertInstanceOf(ToDo.class, incompleteTask),
                () -> assertEquals("read book", incompleteTask.getDescription()),
                () -> assertFalse(incompleteTask.isCompleted()),
                () -> assertInstanceOf(ToDo.class, completedTask),
                () -> assertEquals("submit work", completedTask.getDescription()),
                () -> assertTrue(completedTask.isCompleted())
        );
    }

    @Test
    public void parseTask_validDeadline_restoresAllTaskData() throws GrowerException {
        Storage storage = createStorage();

        Task task = storage.parseTask("D | 1 | submit work | 2026-08-31T23:59:00");

        assertInstanceOf(Deadline.class, task);
        assertTrue(task.isCompleted());
        assertEquals("D | 1 | submit work | 2026-08-31T23:59:00", task.toFileString());
    }

    @Test
    public void parseTask_validEvent_restoresAllTaskData() throws GrowerException {
        Storage storage = createStorage();

        Task task = storage.parseTask(
                "E | 0 | meeting | 2026-08-31T10:00:00 | 2026-08-31T11:30:00");

        assertInstanceOf(Event.class, task);
        assertFalse(task.isCompleted());
        assertEquals(
                "E | 0 | meeting | 2026-08-31T10:00:00 | 2026-08-31T11:30:00",
                task.toFileString());
    }

    @Test
    public void parseTask_missingFields_growerExceptionThrown() {
        Storage storage = createStorage();

        assertAll(
                () -> assertThrows(GrowerException.class, () -> storage.parseTask("T | 0")),
                () -> assertThrows(
                        GrowerException.class,
                        () -> storage.parseTask("D | 0 | submit work")),
                () -> assertThrows(
                        GrowerException.class,
                        () -> storage.parseTask("E | 0 | meeting | 2026-08-31T10:00:00"))
        );
    }

    @Test
    public void parseTask_unknownTypeOrInvalidDate_growerExceptionThrown() {
        Storage storage = createStorage();

        assertAll(
                () -> assertThrows(
                        GrowerException.class,
                        () -> storage.parseTask("N | 0 | unknown task")),
                () -> assertThrows(
                        GrowerException.class,
                        () -> storage.parseTask("D | 0 | submit work | not-a-date")),
                () -> assertThrows(
                        GrowerException.class,
                        () -> storage.parseTask(
                                "E | 0 | meeting | 2026-08-31T10:00:00 | invalid"))
        );
    }

    @Test
    public void parseTask_eventEndNotAfterStart_growerExceptionThrown() {
        Storage storage = createStorage();

        assertAll(
                () -> assertThrows(
                        GrowerException.class,
                        () -> storage.parseTask(
                                "E | 0 | meeting | 2026-08-31T10:00:00 | 2026-08-31T10:00:00")),
                () -> assertThrows(
                        GrowerException.class,
                        () -> storage.parseTask(
                                "E | 0 | meeting | 2026-08-31T10:00:00 | 2026-08-31T09:00:00"))
        );
    }

    @Test
    public void loadTasks_fileDoesNotExist_returnsEmptyList() throws IOException {
        assertEquals(List.of(), createStorage().loadTasks());
    }

    @Test
    public void saveAndLoadTasks_nestedPath_preservesTaskDataAndOrder() throws IOException {
        Storage storage = new Storage(
                temporaryDirectory.resolve("nested/data/grower.txt").toString());
        List<String> taskData = List.of(
                "T | 0 | first",
                "D | 1 | second | 2026-08-31T23:59:00");

        storage.saveTasks(taskData);

        assertEquals(taskData, storage.loadTasks());
    }

    @Test
    public void saveTasks_fileAlreadyHasData_replacesPreviousContents() throws IOException {
        Storage storage = createStorage();
        storage.saveTasks(List.of("T | 0 | old task"));

        storage.saveTasks(List.of("T | 0 | replacement task"));

        assertEquals(List.of("T | 0 | replacement task"), storage.loadTasks());
    }

    @Test
    public void parseTask_invalidFields_rejectsCorruptRecords() {
        for (String line : List.of("", "T | 2 | task", "T | 0 |  ", "T | 0 | task | extra",
                "D | 0 | task | 2027-01-01T12:00:00 | extra", "T | 0 | a|b",
                "T | 0 | a\nb", "T | 0 | a\0b")) {
            assertThrows(GrowerException.class, () -> createStorage().parseTask(line), line);
        }
        assertThrows(GrowerException.class, () -> createStorage().parseTask(null));
    }

    @Test
    public void saveTasks_replacementFails_cleansTemporaryFile() throws IOException {
        Path target = temporaryDirectory.resolve("tasks.txt");
        Files.createDirectory(target);
        Files.writeString(target.resolve("keep.txt"), "original");
        Storage storage = new Storage(target.toString());

        assertThrows(IOException.class, () -> storage.saveTasks(List.of("T | 0 | new")));
        assertEquals("original", Files.readString(target.resolve("keep.txt")));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(target), files.toList());
        }
    }

    @Test
    public void loadTasks_directoryInsteadOfFile_reportsFailure() {
        assertThrows(IOException.class, () -> new Storage(temporaryDirectory.toString()).loadTasks());
    }

    /**
     * Creates storage in the temporary directory assigned to this test.
     */
    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("grower.txt").toString());
    }
}
