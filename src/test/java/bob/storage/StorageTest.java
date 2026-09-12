package bob.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bob.task.Deadline;
import bob.task.Event;
import bob.task.Task;
import bob.task.TaskDateTime;
import bob.task.TaskStatus;
import bob.task.TaskType;
import bob.task.ToDo;

/** Tests task serialization, loading, and malformed storage records. */
class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void load_missingFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(tempDirectory.resolve("missing").resolve("tasks.txt").toString());

        assertEquals(List.of(), storage.load());
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesTaskData() throws IOException {
        Storage storage = new Storage(tempDirectory.resolve("nested").resolve("tasks.txt").toString());
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report",
                new TaskDateTime(LocalDate.of(2019, 12, 2), null));
        Event event = new Event("project meeting",
                new TaskDateTime(LocalDate.of(2019, 12, 2), LocalTime.of(18, 0)),
                new TaskDateTime(LocalDate.of(2019, 12, 2), LocalTime.of(19, 30)));

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertTask(TaskType.TODO, TaskStatus.DONE, "read book", loadedTasks.get(0));
        assertTask(TaskType.DEADLINE, TaskStatus.NOT_DONE, "submit report", loadedTasks.get(1));
        assertEquals(LocalDate.of(2019, 12, 2), ((Deadline) loadedTasks.get(1)).getBy().getDate());
        assertFalse(((Deadline) loadedTasks.get(1)).getBy().getTime().isPresent());
        assertTask(TaskType.EVENT, TaskStatus.NOT_DONE, "project meeting", loadedTasks.get(2));
        assertEquals(LocalTime.of(18, 0), ((Event) loadedTasks.get(2)).getFrom().getTime().orElseThrow());
        assertEquals(LocalTime.of(19, 30), ((Event) loadedTasks.get(2)).getTo().getTime().orElseThrow());
    }

    @Test
    void save_existingData_replacesPreviousContents() throws IOException {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());
        storage.save(List.of(new ToDo("old task")));

        storage.save(List.of(new ToDo("new task")));
        List<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("new task", loadedTasks.get(0).getDescription());
    }

    @Test
    void save_fileWithoutParentDirectory_savesSuccessfully() throws IOException {
        Path filePath = Path.of("storage-test-" + UUID.randomUUID() + ".txt");
        try {
            Storage storage = new Storage(filePath.toString());

            storage.save(List.of(new ToDo("read book")));

            assertEquals("T|0|read book" + System.lineSeparator(), Files.readString(filePath));
        } finally {
            Files.deleteIfExists(filePath);
        }
    }

    @Test
    void save_filePathIsDirectory_ioExceptionIncludesFilePath() {
        Storage storage = new Storage(tempDirectory.toString());

        IOException exception = assertThrows(IOException.class,
                () -> storage.save(List.of(new ToDo("read book"))));

        assertEquals("Could not save tasks to " + tempDirectory + ".", exception.getMessage());
    }

    @Test
    void save_mismatchedDeadlineTask_assertionErrorThrown() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());
        Task mismatchedTask = new TestTask("deadline", TaskType.DEADLINE);

        assertThrows(AssertionError.class, () -> storage.save(List.of(mismatchedTask)));
    }

    @Test
    void save_mismatchedEventTask_assertionErrorThrown() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());
        Task mismatchedTask = new TestTask("event", TaskType.EVENT);

        assertThrows(AssertionError.class, () -> storage.save(List.of(mismatchedTask)));
    }

    @Test
    void deserializeTask_validRecord_returnsTaskWithStoredStatus() throws IOException {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());

        Task task = storage.deserializeTask(new String[]{"T", "1", "read book"}, 0);

        assertTask(TaskType.TODO, TaskStatus.DONE, "read book", task);
    }

    @Test
    void deserializeTask_missingOrUnknownType_ioExceptionThrown() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());

        assertThrows(IOException.class, () -> storage.deserializeTask(new String[]{}, 0));
        assertThrows(IOException.class, () -> storage.deserializeTask(new String[]{"", "0", "task"}, 0));
        assertThrows(IOException.class, () -> storage.deserializeTask(new String[]{"X", "0", "task"}, 0));
    }

    @Test
    void deserializeTask_missingOrInvalidStatus_ioExceptionThrown() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());

        assertThrows(IOException.class, () -> storage.deserializeTask(new String[]{"T"}, 0));
        assertThrows(IOException.class,
                () -> storage.deserializeTask(new String[]{"T", "invalid", "task"}, 0));
    }

    @Test
    void deserializeTask_wrongFieldCount_ioExceptionIncludesOneBasedLineNumber() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());

        IOException exception = assertThrows(IOException.class,
                () -> storage.deserializeTask(new String[]{"D", "0", "submit report"}, 4));

        assertEquals("Could not load saved tasks: corrupted data on line 5 (expected 4 fields but found 3).",
                exception.getMessage());
    }

    @Test
    void deserializeTask_invalidDeadlineDate_ioExceptionIncludesLineNumber() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());

        IOException exception = assertThrows(IOException.class,
                () -> storage.deserializeTask(new String[]{"D", "0", "submit report", "2019-02-30"}, 2));

        assertEquals("Could not load saved tasks: corrupted data on line 3 (invalid deadline date).",
                exception.getMessage());
    }

    @Test
    void deserializeTask_invalidEventDate_ioExceptionIncludesLineNumber() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());

        IOException exception = assertThrows(IOException.class,
                () -> storage.deserializeTask(new String[]{"E", "0", "meeting", "2019-02-30", "2019-03-01"}, 1));

        assertEquals("Could not load saved tasks: corrupted data on line 2 (invalid event date or range).",
                exception.getMessage());
    }

    @Test
    void load_blankLine_ioExceptionIncludesLineNumber() throws IOException {
        Path filePath = tempDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "T|0|read book\n\n", StandardCharsets.UTF_8);

        IOException exception = assertThrows(IOException.class,
                () -> new Storage(filePath.toString()).load());

        assertEquals("Could not load saved tasks: corrupted data on line 2 (missing task type).",
                exception.getMessage());
    }

    @Test
    void load_filePathIsDirectory_ioExceptionThrown() {
        Storage storage = new Storage(tempDirectory.toString());

        assertThrows(IOException.class, storage::load);
    }

    @Test
    void load_impossibleStoredDate_ioExceptionThrown() throws IOException {
        Path filePath = tempDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "D|0|submit report|2024-02-30");

        assertThrows(IOException.class, () -> new Storage(filePath.toString()).load());
    }

    private void assertTask(TaskType expectedType, TaskStatus expectedStatus, String expectedDescription,
            Task actualTask) {
        assertEquals(expectedType, actualTask.getType());
        assertEquals(expectedStatus, actualTask.getStatus());
        assertEquals(expectedDescription, actualTask.getDescription());
    }

    private static class TestTask extends Task {
        TestTask(String description, TaskType type) {
            super(description, type);
        }
    }
}
