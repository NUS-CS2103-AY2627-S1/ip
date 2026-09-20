package duchess.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duchess.task.Deadline;
import duchess.task.Event;
import duchess.task.Task;
import duchess.task.TaskList;
import duchess.task.Todo;

/** Tests persistence, legacy compatibility, and malformed-record handling. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that saving and loading preserves every supported task subtype. */
    @Test
    public void storage_saveAndLoad_preservesTaskTypesAndCompletion() throws Exception {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("duchess.txt");
        Storage storage = new Storage(dataFile);
        Task completedTodo = new Todo("read book");
        completedTodo.markAsDone(Instant.parse("2026-09-01T00:00:00Z"));
        TaskList tasks = new TaskList(
                completedTodo,
                new Deadline("return book", "2019-12-02"),
                new Event("buy bread", "Saturday"));

        storage.saveTasks(tasks);
        TaskList restored = storage.loadTasks();

        assertAll(
                () -> assertEquals(3, restored.size()),
                () -> assertTrue(restored.get(0) instanceof Todo),
                () -> assertTrue(restored.get(0).isDone()),
                () -> assertEquals("read book", restored.get(0).getDescription()),
                () -> assertEquals(Instant.parse("2026-09-01T00:00:00Z"), restored.get(0).getCompletedAt()),
                () -> assertEquals(LocalDate.of(2019, 12, 2), ((Deadline) restored.get(1)).getBy()),
                () -> assertEquals("Saturday", ((Event) restored.get(2)).getAt())
        );
    }

    /** Verifies that malformed lines are ignored without discarding valid records. */
    @Test
    public void storage_malformedRecord_isIgnored() throws Exception {
        Path dataFile = temporaryDirectory.resolve("duchess.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(new TaskList(new Todo("valid task")));
        Files.writeString(dataFile, Files.readString(dataFile) + "\nnot a task record\n", StandardCharsets.UTF_8);

        TaskList restored = storage.loadTasks();

        assertEquals(1, restored.size());
        assertEquals("valid task", restored.get(0).getDescription());
    }

    /** Verifies that duplicate records in an unexpected data file are loaded once. */
    @Test
    public void storage_duplicateRecords_loadsSingleTask() throws Exception {
        Path dataFile = temporaryDirectory.resolve("duchess.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(new TaskList(new Todo("read book")));
        String record = Files.readString(dataFile, StandardCharsets.UTF_8);
        Files.writeString(dataFile, record + record, StandardCharsets.UTF_8);

        TaskList restored = storage.loadTasks();

        assertEquals(1, restored.size());
        assertEquals("read book", restored.get(0).getDescription());
    }

    /** Verifies that saving an empty list creates a readable empty data file. */
    @Test
    public void storage_emptyTaskList_savesAndLoadsEmptyFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("empty.txt");
        Storage storage = new Storage(dataFile);

        storage.saveTasks(new TaskList());

        assertTrue(Files.isRegularFile(dataFile));
        assertEquals(0, Files.size(dataFile));
        assertEquals(0, storage.loadTasks().size());
    }

    /** Verifies that completed records from the legacy format remain readable. */
    @Test
    public void storage_legacyCompletedRecord_loadsWithoutTimestamp() throws Exception {
        Path dataFile = temporaryDirectory.resolve("duchess.txt");
        String encodedDescription = Base64.getEncoder().encodeToString(
                "legacy task".getBytes(StandardCharsets.UTF_8));
        Files.writeString(dataFile, "T|1|" + encodedDescription + "\n", StandardCharsets.UTF_8);

        Task restored = new Storage(dataFile).loadTasks().get(0);

        assertAll(
                () -> assertTrue(restored.isDone()),
                () -> assertNull(restored.getCompletedAt()),
                () -> assertFalse(restored.getDescription().isBlank())
        );
    }

    /** Verifies that a missing data file produces an empty task list. */
    @Test
    public void storage_missingFile_returnsEmptyTaskList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));
        TaskList restored = storage.loadTasks();

        assertEquals(0, restored.size());
        assertEquals("", storage.getLoadWarning());
    }

    /** Verifies an unreadable file cannot be overwritten until a successful reload. */
    @Test
    public void storage_failedLoad_blocksSaveUntilRecovery() throws Exception {
        Path dataFile = temporaryDirectory.resolve("damaged.txt");
        byte[] damagedBytes = {(byte) 0xC3, (byte) 0x28};
        Files.write(dataFile, damagedBytes);
        Storage storage = new Storage(dataFile);
        storage.loadTasks();

        assertTrue(storage.getLoadWarning().contains(dataFile.toString()));
        assertThrows(IOException.class, () -> storage.saveTasks(new TaskList(new Todo("new"))));
        assertArrayEquals(damagedBytes, Files.readAllBytes(dataFile));

        Files.writeString(dataFile, "\n");
        assertEquals(0, storage.loadTasks().size());
        assertEquals("", storage.getLoadWarning());
        storage.saveTasks(new TaskList(new Todo("recovered")));
        assertEquals("recovered", storage.loadTasks().get(0).getDescription());
    }

    /** Verifies that a directory at the data-file location is treated as unreadable storage. */
    @Test
    public void storage_dataPathIsDirectory_returnsEmptyTaskList() throws Exception {
        Path dataDirectory = temporaryDirectory.resolve("duchess.txt");
        Files.createDirectory(dataDirectory);

        TaskList restored = new Storage(dataDirectory).loadTasks();

        assertEquals(0, restored.size());
    }

    /** Verifies that undecodable UTF-8 data cannot prevent startup. */
    @Test
    public void storage_invalidUtf8_returnsEmptyTaskList() throws Exception {
        Path dataFile = temporaryDirectory.resolve("duchess.txt");
        Files.write(dataFile, new byte[]{(byte) 0xC3, (byte) 0x28});

        TaskList restored = new Storage(dataFile).loadTasks();

        assertEquals(0, restored.size());
    }

    /** Verifies date ranges survive a restart alongside legacy events and completion times. */
    @Test
    public void storage_eventRange_preservesDatesAndCompletion() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("events.txt"));
        Event event = new Event("orientation | tour", "2026-09-15", "2026-09-17");
        Instant completedAt = Instant.parse("2026-09-17T12:00:00Z");
        event.markAsDone(completedAt);
        storage.saveTasks(new TaskList(event, new Event("old meeting", "Monday")));

        TaskList restored = storage.loadTasks();
        Event restoredEvent = (Event) restored.get(0);
        assertEquals(2, restored.size());
        assertEquals(event.toString(), restoredEvent.toString());
        assertEquals(event.getFrom(), restoredEvent.getFrom());
        assertEquals(event.getTo(), restoredEvent.getTo());
        assertEquals(completedAt, restoredEvent.getCompletedAt());
        assertEquals("Monday", ((Event) restored.get(1)).getAt());
    }

    /** Verifies corrupt dated events are skipped without losing adjacent valid tasks. */
    @Test
    public void storage_invalidEventDates_skipsRecord() throws Exception {
        Path dataFile = temporaryDirectory.resolve("events.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(new TaskList(new Event("meeting", "2026-09-15", "2026-09-17"), new Todo("keep")));
        String invalidDate = Base64.getEncoder().encodeToString("2026-02-30".getBytes(StandardCharsets.UTF_8));
        String validDate = Base64.getEncoder().encodeToString("2026-09-15".getBytes(StandardCharsets.UTF_8));
        Files.writeString(dataFile, Files.readString(dataFile).replace(validDate, invalidDate));
        TaskList restored = storage.loadTasks();
        assertEquals(1, restored.size());
        assertEquals("keep", restored.get(0).getDescription());
    }

    /** Verifies every malformed record shape is skipped while valid adjacent data survives. */
    @Test
    public void storage_malformedRecordVariants_skipsAllInvalidRecords() throws Exception {
        Path dataFile = temporaryDirectory.resolve("malformed.txt");
        String description = encode("broken task");
        String validDate = encode("2026-09-15");
        String laterDate = encode("2026-09-17");
        String[] records = {
            "",
            "T|0",
            "X|0|" + description,
            "T|2|" + description,
            "T|0|%%%",
            "T|0|" + encode("   "),
            "T|0|" + description + "|extra|field",
            "D|0|" + description,
            "D|0|" + description + "|%%%",
            "D|0|" + description + "|" + encode(" "),
            "D|0|" + description + "|" + encode("2026-02-30"),
            "E|0|" + description + "|%%%|" + laterDate + "|",
            "E|0|" + description + "|" + validDate + "|%%%|",
            "E|0|" + description + "|" + validDate + "|" + validDate + "|",
            "E|0|" + description + "|%%%",
            "E|0|" + description + "|" + encode(" "),
            "E|0|" + description,
            "T|0|" + encode("keep task")
        };
        Files.write(dataFile, java.util.List.of(records), StandardCharsets.UTF_8);

        TaskList restored = new Storage(dataFile).loadTasks();

        assertEquals(1, restored.size());
        assertEquals("keep task", restored.get(0).getDescription());
    }

    /** Verifies malformed completion times preserve completion status without inventing a time. */
    @Test
    public void storage_invalidCompletionTimes_loadsCompletedTasksWithoutTimestamps() throws Exception {
        Path dataFile = temporaryDirectory.resolve("completion-times.txt");
        String[] records = {
            "T|1|" + encode("todo") + "|%%%",
            "D|1|" + encode("deadline") + "|" + encode("2026-09-30") + "|" + encode("not-an-instant"),
            "E|1|" + encode("event") + "|" + encode("Monday") + "|"
        };
        Files.write(dataFile, java.util.List.of(records), StandardCharsets.UTF_8);

        TaskList restored = new Storage(dataFile).loadTasks();

        assertEquals(3, restored.size());
        for (int index = 0; index < restored.size(); index++) {
            assertTrue(restored.get(index).isDone());
            assertNull(restored.get(index).getCompletedAt());
        }
    }

    /** Verifies storage rejects null collaborators through its documented assertions. */
    @Test
    public void storage_nullInputs_throwAssertionError() {
        assertAll(
                () -> assertThrows(AssertionError.class, () -> new Storage(null)),
                () -> assertThrows(AssertionError.class,
                        () -> new Storage(temporaryDirectory.resolve("tasks.txt")).saveTasks(null))
        );
    }

    /** Encodes one field using the storage file's Base64 convention. */
    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
