package bo.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bo.model.Deadline;
import bo.model.Event;
import bo.model.Task;
import bo.model.Todo;

/** Tests persistence, escaping, malformed-record handling, and capacity limits. */
class StorageTest {
    /** The storage file used by the application when tests run from the project root. */
    private final Path taskFile = Path.of("data", "duke.txt").toAbsolutePath().normalize();

    /** The original storage file contents, restored after each test. */
    private byte[] originalContents;

    /** Whether the storage file existed before the test started. */
    private boolean fileExisted;

    /** Preserves the user's task data and starts each test without a storage file. */
    @BeforeEach
    void preserveStorageFile() throws IOException {
        fileExisted = Files.exists(taskFile);
        originalContents = fileExisted ? Files.readAllBytes(taskFile) : null;
        Files.deleteIfExists(taskFile);
    }

    /** Restores the user's task data after the test has completed. */
    @AfterEach
    void restoreStorageFile() throws IOException {
        if (fileExisted) {
            Files.deleteIfExists(taskFile);
            Files.write(taskFile, originalContents);
        } else {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies round-trip persistence for all task types, statuses, and escaped fields. */
    @Test
    void saveAndLoad_allTaskTypes_roundTripsData() throws IOException {
        Todo todo = new Todo("read | book\\notes");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 28));
        Event event = new Event("project sync", LocalDateTime.of(2026, 8, 29, 9, 5),
                LocalDateTime.of(2026, 8, 29, 10, 30));
        Task[] tasks = {todo, deadline, event};

        Storage.save(tasks, tasks.length);
        Task[] loaded = new Task[3];
        Storage.LoadResult result = Storage.loadWithReport(loaded);

        assertAll(
                () -> assertEquals("T | 1 | read \\| book\\\\notes\n"
                        + "D | 0 | return book | 2026-08-28\n"
                        + "E | 0 | project sync | 2026-08-29T09:05:00 | 2026-08-29T10:30:00\n",
                        Files.readString(taskFile, StandardCharsets.UTF_8)),
                () -> assertEquals(3, result.getTaskCount()),
                () -> assertEquals(0, result.getInvalidLineCount()),
                () -> assertEquals(0, result.getExcessTaskCount()),
                () -> assertEquals("read | book\\notes", loaded[0].getDescription()),
                () -> assertInstanceOf(Todo.class, loaded[0]),
                () -> assertEquals(true, loaded[0].isDone()),
                () -> assertEquals("2026-08-28", assertInstanceOf(Deadline.class, loaded[1]).getStoredBy()),
                () -> assertEquals("2026-08-29T09:05:00", assertInstanceOf(Event.class, loaded[2]).getStoredFrom()));
    }

    /** Verifies that malformed lines are skipped while later valid lines are loaded. */
    @Test
    void loadWithReport_malformedRecords_skipsInvalidLines() throws IOException {
        writeStorage("T | 1 | valid todo\n"
                + "not a task record\n"
                + "D | 0 | valid deadline | Friday\n"
                + "E | 2 | invalid status | Monday | Tuesday\n"
                + "E | 0 | valid event | Monday | Tuesday\n\n");

        Task[] loaded = new Task[5];
        Storage.LoadResult result = Storage.loadWithReport(loaded);

        assertAll(
                () -> assertEquals(3, result.getTaskCount()),
                () -> assertEquals(2, result.getInvalidLineCount()),
                () -> assertEquals(0, result.getExcessTaskCount()),
                () -> assertEquals("valid todo", loaded[0].getDescription()),
                () -> assertEquals(true, loaded[0].isDone()),
                () -> assertEquals("valid deadline", loaded[1].getDescription()),
                () -> assertEquals("valid event", loaded[2].getDescription()));
    }

    /** Verifies that impossible typed dates and event ranges are skipped as malformed records. */
    @Test
    void loadWithReport_invalidTypedDates_skipsInvalidLines() throws IOException {
        writeStorage("D | 0 | impossible date | 2019-02-29\n"
                + "E | 0 | reversed event | 2019-10-16T10:00:00 | 2019-10-16T09:00:00\n"
                + "T | 0 | valid todo\n");

        Task[] loaded = new Task[3];
        Storage.LoadResult result = Storage.loadWithReport(loaded);

        assertAll(
                () -> assertEquals(1, result.getTaskCount()),
                () -> assertEquals(2, result.getInvalidLineCount()),
                () -> assertEquals("valid todo", loaded[0].getDescription()));
    }

    /** Verifies that valid records beyond the supplied array capacity are reported. */
    @Test
    void loadWithReport_moreRecordsThanCapacity_reportsExcessRecords() throws IOException {
        writeStorage("T | 0 | first\nT | 0 | second\nT | 0 | third\n");

        Task[] loaded = new Task[2];
        Storage.LoadResult result = Storage.loadWithReport(loaded);

        assertAll(
                () -> assertEquals(2, result.getTaskCount()),
                () -> assertEquals(0, result.getInvalidLineCount()),
                () -> assertEquals(1, result.getExcessTaskCount()),
                () -> assertEquals("first", loaded[0].getDescription()),
                () -> assertEquals("second", loaded[1].getDescription()));
    }

    /** Verifies that a missing file produces an empty result and clears stale array entries. */
    @Test
    void loadWithReport_missingFile_returnsEmptyResult() throws IOException {
        Task[] loaded = {new Todo("stale task"), null};

        Storage.LoadResult result = Storage.loadWithReport(loaded);

        assertAll(
                () -> assertEquals(0, result.getTaskCount()),
                () -> assertEquals(0, result.getInvalidLineCount()),
                () -> assertEquals(0, result.getExcessTaskCount()),
                () -> assertNull(loaded[0]),
                () -> assertNull(loaded[1]));
    }

    /** Verifies that a storage path that cannot be opened as a file reports an I/O error. */
    @Test
    void loadWithReport_storagePathIsDirectory_throwsIoException() throws IOException {
        Files.createDirectory(taskFile);

        assertThrows(IOException.class, () -> Storage.loadWithReport(new Task[1]));
    }

    /** Verifies that save rejects invalid arrays, counts, and task descriptions. */
    @Test
    void save_invalidArguments_throwsException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> Storage.save(null, 0)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Storage.save(new Task[1], -1)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Storage.save(new Task[1], 2)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Storage.save(new Task[] {null}, 1)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Storage.save(new Task[] {new Todo(" ")}, 1)));
    }

    /** Writes a fixture using the same UTF-8 encoding as the application. */
    private void writeStorage(String contents) throws IOException {
        Files.writeString(taskFile, contents, StandardCharsets.UTF_8);
    }
}
