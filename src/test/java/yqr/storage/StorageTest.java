package yqr.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yqr.exception.YqrException;
import yqr.task.Deadline;
import yqr.task.Event;
import yqr.task.TaskList;
import yqr.task.Todo;

/**
 * Tests storage behavior for missing, malformed, and unwritable data files.
 */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void loadTasks_missingFile_emptyTaskListReturned() throws YqrException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        TaskList taskList = storage.loadTasks();

        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    void loadTasks_validFile_tasksAndStatusesRestored() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 1 | read book\nD | 0 | submit | 2026-09-30\n",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        TaskList taskList = storage.loadTasks();

        assertEquals(2, taskList.getTaskCount());
        assertTrue(taskList.getTasks().get(0).isDone());
    }

    @Test
    void loadTasks_blankDescription_invalidDataReported() throws Exception {
        assertInvalidSavedData("T | 0 | ", "Invalid saved task on line 1");
    }

    @Test
    void loadTasks_impossibleDate_invalidDataReported() throws Exception {
        assertInvalidSavedData("D | 0 | submit | 2026-02-30", "Invalid saved task on line 1");
    }

    @Test
    void loadTasks_reversedEventRange_invalidDataReported() throws Exception {
        assertInvalidSavedData("E | 0 | meeting | 11:00 | 10:00",
                "Invalid saved task on line 1");
    }

    @Test
    void loadTasks_duplicateRecords_duplicateLineReported() throws Exception {
        assertInvalidSavedData("T | 0 | read book\nT | 1 | read book",
                "Duplicate saved task on line 2");
    }

    @Test
    void loadTasks_pathIsDirectory_ioErrorReported() {
        Storage storage = new Storage(temporaryDirectory);

        YqrException exception = assertThrows(YqrException.class, storage::loadTasks);

        assertTrue(exception.getMessage().startsWith("Unable to load saved tasks:"));
    }

    @Test
    void saveTasks_parentPathIsFile_ioErrorReported() throws Exception {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "content", StandardCharsets.UTF_8);
        Storage storage = new Storage(parentFile.resolve("tasks.txt"));

        YqrException exception = assertThrows(
                YqrException.class, () -> storage.saveTasks(new TaskList()));

        assertTrue(exception.getMessage().startsWith("Unable to save tasks:"));
    }

    @Test
    void saveTasks_unsupportedText_rejectedBeforeWriting() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList taskList = new TaskList(List.of(new Todo("read | write")));

        YqrException exception = assertThrows(
                YqrException.class, () -> storage.saveTasks(taskList));

        assertEquals("Unable to save tasks: task details contain unsupported text",
                exception.getMessage());
    }

    @Test
    void saveTasks_duplicateTaskDetails_rejectedBeforeWriting() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList taskList = new TaskList(List.of(new Todo("read"), new Todo("read")));

        YqrException exception = assertThrows(
                YqrException.class, () -> storage.saveTasks(taskList));

        assertEquals("Unable to save tasks: duplicate task details", exception.getMessage());
    }

    @Test
    void saveTasks_reversedEventRange_rejectedBeforeWriting() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList taskList = new TaskList(List.of(new Event("meeting", "11:00", "10:00")));

        YqrException exception = assertThrows(
                YqrException.class, () -> storage.saveTasks(taskList));

        assertEquals("Event start must be earlier than its end", exception.getMessage());
    }

    @Test
    void saveTasks_validTaskTypes_roundTripPreservesAllTasks() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        TaskList original = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("submit report", LocalDate.of(2026, 9, 30)),
                new Event("meeting", "09:00", "10:00")));

        storage.saveTasks(original);
        TaskList restored = storage.loadTasks();

        assertEquals(original.getTasks().stream().map(Object::toString).toList(),
                restored.getTasks().stream().map(Object::toString).toList());
    }

    /** Writes raw storage text and asserts the resulting loading error. */
    private void assertInvalidSavedData(String content, String expectedMessage) throws Exception {
        Path dataFile = temporaryDirectory.resolve("invalid.txt");
        Files.writeString(dataFile, content, StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        YqrException exception = assertThrows(YqrException.class, storage::loadTasks);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
