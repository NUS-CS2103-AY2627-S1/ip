package tangent.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tangent.exception.ErrorMessages;
import tangent.exception.TangentException;
import tangent.task.Deadline;
import tangent.task.Event;
import tangent.task.Task;
import tangent.task.ToDo;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_emptyListReturnedAndFileCreated() throws TangentException {
        Path dataFile = tempDir.resolve("data").resolve("tangent.txt");
        Storage storage = new Storage(dataFile.toString());
        List<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
        assertTrue(Files.exists(dataFile));
    }

    @Test
    public void load_validRecords_correctTaskTypesFieldsAndStatusesReturned() throws IOException, TangentException {
        Path dataFile = tempDir.resolve("tangent.txt");
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "T | 0 | borrow book",
                "D | 1 | return book | 2/12/2019 1800",
                "E | 0 | project meeting | 3/12/2019 0900 | 3/12/2019 1100"));

        Storage storage = new Storage(dataFile.toString());
        List<Task> tasks = storage.load();

        assertEquals(3, tasks.size());

        ToDo todo = assertInstanceOf(ToDo.class, tasks.get(0));
        assertEquals("borrow book", todo.getDescription());
        assertFalse(todo.isDone());

        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(1));
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertTrue(deadline.isDone());

        Event event = assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 3, 9, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 11, 0), event.getTo());
        assertFalse(event.isDone());
    }

    @Test
    public void load_unknownTaskType_exceptionThrown() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        Files.writeString(dataFile, "X | 0 | thing");

        Storage storage = new Storage(dataFile.toString());

        TangentException exception = assertThrows(TangentException.class, storage::load);

        assertEquals(String.format(ErrorMessages.UNKNOWN_STORED_TASK_TYPE_MESSAGE, "X"), exception.getMessage());
    }

    @Test
    public void load_invalidStatus_exceptionThrown() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        Files.writeString(dataFile, "T | 2 | borrow book");

        Storage storage = new Storage(dataFile.toString());

        TangentException exception = assertThrows(TangentException.class, storage::load);

        assertEquals(String.format(ErrorMessages.INVALID_TASK_RECORD_MESSAGE, "T | 2 | borrow book"),
                exception.getMessage());
    }

    @Test
    public void load_invalidDate_exceptionThrown() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        Files.writeString(dataFile, "D | 0 | return book | 31/2/2019 1800");

        Storage storage = new Storage(dataFile.toString());

        TangentException exception = assertThrows(TangentException.class, storage::load);

        assertEquals(String.format(ErrorMessages.INVALID_STORED_DATE_MESSAGE,
                "D | 0 | return book | 31/2/2019 1800"), exception.getMessage());
    }

    @Test
    public void load_invalidEventTimeRange_exceptionThrown() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        Files.writeString(dataFile, "E | 0 | meeting | 3/12/2019 1100 | 3/12/2019 0900");

        TangentException exception = assertThrows(TangentException.class, () -> new Storage(
                dataFile.toString()).load());

        assertTrue(exception.getMessage().startsWith(
                String.format(ErrorMessages.INVALID_STORED_EVENT_RANGE_MESSAGE, "")));
    }

    @Test
    public void load_emptyDescription_exceptionThrown() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        Files.writeString(dataFile, "T | 0 |   ");

        TangentException exception = assertThrows(TangentException.class, () -> new Storage(
                dataFile.toString()).load());

        assertTrue(exception.getMessage().startsWith(String.format(ErrorMessages.INVALID_TASK_RECORD_MESSAGE, "")));
    }

    @Test
    public void save_missingParentDirectory_fileCreated() throws Exception {
        Path dataFile = tempDir.resolve("nested").resolve("tangent.txt");
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of(new ToDo("borrow book")));

        assertEquals("T | 0 | borrow book" + System.lineSeparator(), Files.readString(dataFile));
    }

    @Test
    public void save_allTaskTypesAndStatuses_roundTripThroughFile() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("project meeting", LocalDateTime.of(2019, 12, 3, 9, 0),
                LocalDateTime.of(2019, 12, 3, 11, 0));

        Storage storage = new Storage(dataFile.toString());
        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(List.of("T | 1 | read book", "D | 0 | return book | 2/12/2019 1800",
                "E | 0 | project meeting | 3/12/2019 0900 | 3/12/2019 1100"), Files.readAllLines(dataFile));
        assertTrue(loaded.get(0).isDone());
        assertFalse(loaded.get(1).isDone());
        assertEquals("return book", loaded.get(1).getDescription());
        assertEquals("project meeting", loaded.get(2).getDescription());
    }

    @Test
    public void save_nullTaskList_throwsMissingTaskList() {
        TangentException exception = assertThrows(TangentException.class, () -> new Storage(
                tempDir.resolve("tangent.txt").toString()).save(null));

        assertEquals(ErrorMessages.MISSING_TASK_LIST_MESSAGE, exception.getMessage());
    }

    @Test
    public void save_invalidTasks_throwsInvalidTaskError() {
        Storage storage = new Storage(tempDir.resolve("tangent.txt").toString());

        assertInvalidTask(storage, null);
        assertInvalidTask(storage, new Task("   "));
        assertInvalidTask(storage, new Task("buy | milk"));
        assertInvalidTask(storage, new Deadline("deadline", null));
        assertInvalidTask(storage, new Event("event", null, LocalDateTime.of(2020, 1, 1, 10, 0)));
        assertInvalidTask(storage, new Event("event", LocalDateTime.of(2020, 1, 1, 10, 0),
                LocalDateTime.of(2020, 1, 1, 9, 0)));
    }

    @Test
    public void save_emptyTaskList_clearsFile() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        Storage storage = new Storage(dataFile.toString());
        storage.save(List.of(new ToDo("old task")));

        storage.save(List.of());

        assertTrue(Files.exists(dataFile));
        assertTrue(Files.readAllLines(dataFile).isEmpty());
    }

    @Test
    public void load_wrongFieldCounts_throwsInvalidRecord() throws Exception {
        assertInvalidRecord("T | 0 | task | extra");
        assertInvalidRecord("D | 0 | task");
        assertInvalidRecord("D | 0 | task | 2/12/2019 1800 | extra");
        assertInvalidRecord("E | 0 | task | 3/12/2019 0900");
        assertInvalidRecord("E | 0 | task | 3/12/2019 0900 | 3/12/2019 1100 | extra");
    }

    @Test
    public void load_invalidEventDate_throwsBadDate() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        Files.writeString(dataFile, "E | 0 | meeting | 31/2/2019 0900 | 3/12/2019 1100");

        TangentException exception = assertThrows(TangentException.class, () -> new Storage(
                dataFile.toString()).load());

        assertEquals(String.format(ErrorMessages.INVALID_STORED_DATE_MESSAGE,
                "E | 0 | meeting | 31/2/2019 0900 | 3/12/2019 1100"), exception.getMessage());
    }

    @Test
    public void load_orSaveDirectoryPath_throwsDirectoryError() {
        Path directory = tempDir.resolve("directory");
        assertDirectory(directory);
        Storage storage = new Storage(directory.toString());

        TangentException loadException = assertThrows(TangentException.class, storage::load);
        TangentException saveException = assertThrows(TangentException.class, () -> storage.save(List.of()));

        String expected = String.format(ErrorMessages.DATA_PATH_DIRECTORY_MESSAGE,
                directory.toAbsolutePath().normalize());
        assertEquals(expected, loadException.getMessage());
        assertEquals(expected, saveException.getMessage());
    }

    @Test
    public void load_parentPathIsFile_reportsReadFailure() throws Exception {
        Path parentFile = tempDir.resolve("not-a-directory");
        Files.writeString(parentFile, "content");
        Storage storage = new Storage(parentFile.resolve("tangent.txt").toString());

        TangentException exception = assertThrows(TangentException.class, storage::load);

        assertTrue(exception.getMessage().startsWith("unable to read data file"));
    }

    @Test
    public void save_parentPathIsFile_reportsSaveFailure() throws Exception {
        Path parentFile = tempDir.resolve("not-a-directory");
        Files.writeString(parentFile, "content");
        Storage storage = new Storage(parentFile.resolve("tangent.txt").toString());

        TangentException exception = assertThrows(TangentException.class, () -> storage.save(List.of()));

        assertTrue(exception.getMessage().startsWith("unable to save data file"));
    }

    private void assertInvalidTask(Storage storage, Task task) {
        TangentException exception = assertThrows(TangentException.class, () ->
                storage.save(Collections.singletonList(task)));
        assertEquals(ErrorMessages.INVALID_TASK_TO_SAVE_MESSAGE, exception.getMessage());
    }

    private void assertInvalidRecord(String record) throws IOException {
        Path dataFile = tempDir.resolve("tangent.txt");
        Files.writeString(dataFile, record);

        TangentException exception = assertThrows(TangentException.class, () -> new Storage(
                dataFile.toString()).load());

        assertEquals(String.format(ErrorMessages.INVALID_TASK_RECORD_MESSAGE, record), exception.getMessage());
    }

    private void assertDirectory(Path directory) {
        try {
            Files.createDirectory(directory);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
