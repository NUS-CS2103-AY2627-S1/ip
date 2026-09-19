package lizzy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lizzy.exception.LizzyException;
import lizzy.task.Deadline;
import lizzy.task.Event;
import lizzy.task.Task;
import lizzy.task.Todo;
import lizzy.task.WithinPeriodTask;

/**
 * Tests task-file creation, round-trip persistence, and invalid-data handling.
 */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_emptyTaskListReturned() throws LizzyException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/lizzy.txt"));

        assertEquals(List.of(), storage.load());
    }

    @Test
    void saveAndLoad_allTaskTypesAndStatesPreserved() throws LizzyException {
        Path filePath = temporaryDirectory.resolve("nested/data/lizzy.txt");
        Storage storage = new Storage(filePath);
        Todo todo = new Todo("read | reference", true);
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 10));
        Event event = new Event("project meeting", LocalDate.of(2026, 9, 11),
                LocalDate.of(2026, 9, 12), true);
        Event timedEvent = new Event("team meeting", LocalDate.of(2026, 9, 16),
                LocalTime.of(14, 0), LocalTime.of(15, 30));
        WithinPeriodTask periodTask = new WithinPeriodTask("collect certificate", LocalDate.of(2026, 9, 13),
                LocalDate.of(2026, 9, 15));

        storage.save(List.of(todo, deadline, event, timedEvent, periodTask));
        List<Task> loadedTasks = storage.load();

        assertEquals(5, loadedTasks.size());
        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read | reference", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals(LocalDate.of(2026, 9, 10), loadedDeadline.getBy());
        assertFalse(loadedDeadline.isDone());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals(LocalDate.of(2026, 9, 11), loadedEvent.getFrom());
        assertEquals(LocalDate.of(2026, 9, 12), loadedEvent.getTo());
        assertTrue(loadedEvent.isDone());

        Event loadedTimedEvent = assertInstanceOf(Event.class, loadedTasks.get(3));
        assertEquals(LocalDate.of(2026, 9, 16), loadedTimedEvent.getFrom());
        assertEquals(LocalTime.of(14, 0), loadedTimedEvent.getStartTime().orElseThrow());
        assertEquals(LocalTime.of(15, 30), loadedTimedEvent.getEndTime().orElseThrow());

        WithinPeriodTask loadedPeriodTask = assertInstanceOf(WithinPeriodTask.class, loadedTasks.get(4));
        assertEquals(LocalDate.of(2026, 9, 13), loadedPeriodTask.getFrom());
        assertEquals(LocalDate.of(2026, 9, 15), loadedPeriodTask.getTo());
        assertFalse(loadedPeriodTask.isDone());
        assertTrue(Files.exists(filePath));
    }

    @Test
    void load_invalidRecord_friendlyExceptionIdentifiesLine() throws IOException {
        Path filePath = temporaryDirectory.resolve("lizzy.txt");
        Files.writeString(filePath, "not valid\n");
        Storage storage = new Storage(filePath);

        LizzyException exception = assertThrows(LizzyException.class, storage::load);

        assertTrue(exception.getMessage().contains("saved task data at line 1"));
    }

    @Test
    void load_blankDescription_friendlyExceptionIdentifiesLine() throws IOException {
        Path filePath = temporaryDirectory.resolve("lizzy.txt");
        Files.writeString(filePath, "T | 0 | IA==\n");
        Storage storage = new Storage(filePath);

        LizzyException exception = assertThrows(LizzyException.class, storage::load);

        assertTrue(exception.getMessage().contains("saved task data at line 1"));
    }

    @Test
    void load_reversedEventDates_friendlyExceptionIdentifiesLine() throws IOException {
        Path filePath = temporaryDirectory.resolve("lizzy.txt");
        Files.writeString(filePath,
                "E | 0 | bWVldGluZw== | MjAyNi0wOS0xMg== | MjAyNi0wOS0xMQ==\n");
        Storage storage = new Storage(filePath);

        LizzyException exception = assertThrows(LizzyException.class, storage::load);

        assertTrue(exception.getMessage().contains("saved task data at line 1"));
    }

    @Test
    void load_malformedRecordVariants_eachRejectedAsInvalidSavedData() throws IOException {
        List<String> invalidRecords = List.of(
                "T | 2 | " + encode("read chapter"),
                "X | 0 | " + encode("read chapter"),
                "T | 0",
                "D | 0 | " + encode("submit report") + " | " + encode("2026-09-20")
                        + " | extra",
                "T | 0 | %%%",
                "D | 0 | " + encode("submit report") + " | " + encode("2026-02-30"),
                "W | 0 | " + encode("collect certificate") + " | " + encode("2026-09-22")
                        + " | " + encode("2026-09-20"));

        for (String invalidRecord : invalidRecords) {
            Path filePath = temporaryDirectory.resolve("invalid-" + invalidRecords.indexOf(invalidRecord) + ".txt");
            Files.writeString(filePath, invalidRecord + System.lineSeparator());

            LizzyException exception = assertThrows(LizzyException.class, () -> new Storage(filePath).load());

            assertTrue(exception.getMessage().contains("saved task data at line 1"));
        }
    }

    @Test
    void load_blankLinesBeforeInvalidRecord_reportUsesPhysicalLineNumber() throws IOException {
        Path filePath = temporaryDirectory.resolve("lizzy.txt");
        Files.writeString(filePath, System.lineSeparator() + System.lineSeparator() + "not valid\n");

        LizzyException exception = assertThrows(LizzyException.class, () -> new Storage(filePath).load());

        assertTrue(exception.getMessage().contains("saved task data at line 3"));
    }

    @Test
    void save_emptyListAfterExistingTasks_fileClearedAndEmptyListLoaded() throws LizzyException, IOException {
        Path filePath = temporaryDirectory.resolve("lizzy.txt");
        Storage storage = new Storage(filePath);
        storage.save(List.of(new Todo("read chapter")));

        storage.save(List.of());

        assertEquals("", Files.readString(filePath));
        assertEquals(List.of(), storage.load());
    }

    @Test
    void load_pathIsDirectory_friendlyReadErrorReturned() throws IOException {
        Path directoryPath = temporaryDirectory.resolve("tasks");
        Files.createDirectory(directoryPath);
        Storage storage = new Storage(directoryPath);

        LizzyException exception = assertThrows(LizzyException.class, storage::load);

        assertTrue(exception.getMessage().contains("couldn't read your saved tasks"));
    }

    /**
     * Encodes a storage field using the same documented UTF-8 Base64 representation as production storage.
     *
     * @param value the plain-text field value
     * @return the encoded storage field
     */
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
