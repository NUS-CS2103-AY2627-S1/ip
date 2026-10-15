package snoopy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import snoopy.exception.SnoopyException;
import snoopy.task.Deadline;
import snoopy.task.Event;
import snoopy.task.TaskList;
import snoopy.task.Todo;

/**
 * Tests saving, loading, and validation of persisted tasks.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void constructor_defaultPath_createsStorageInstance() {
        assertNotNull(new Storage());
    }

    @Test
    public void load_missingDataFile_returnsEmptyList() throws IOException, SnoopyException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/snoopy.txt"));

        assertEquals(0, storage.load().size());
    }

    @Test
    public void save_emptyTaskList_createsNestedEmptyFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested/data/snoopy.txt");
        Storage storage = new Storage(dataFile);

        storage.save(new TaskList());

        assertTrue(Files.exists(dataFile));
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    public void load_emptyDataFile_returnsEmptyList() throws IOException, SnoopyException {
        Path dataFile = temporaryDirectory.resolve("empty.txt");
        Files.createFile(dataFile);
        Storage storage = new Storage(dataFile);

        assertEquals(0, storage.load().size());
    }

    @Test
    public void saveAndLoad_allTaskTypes_preservesOrderDetailsAndStatus()
            throws IOException, SnoopyException {
        Path dataFile = temporaryDirectory.resolve("nested/data/snoopy.txt");
        Storage storage = new Storage(dataFile);
        TaskList originalTasks = new TaskList();
        originalTasks.add(new Todo("read book"));
        originalTasks.add(new Deadline("return book", LocalDate.of(2026, 8, 30)));
        originalTasks.add(new Event("meeting", LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 2)));
        originalTasks.get(1).markAsDone();

        storage.save(originalTasks);
        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("T | 0 | read book", loadedTasks.get(0).toDataString());
        assertEquals("D | 1 | return book | 2026-08-30", loadedTasks.get(1).toDataString());
        assertEquals("E | 0 | meeting | 2026-09-01 | 2026-09-02",
                loadedTasks.get(2).toDataString());
        assertEquals(originalTasks.get(0).toString(), loadedTasks.get(0).toString());
        assertEquals(originalTasks.get(1).toString(), loadedTasks.get(1).toString());
        assertEquals(originalTasks.get(2).toString(), loadedTasks.get(2).toString());
    }

    @Test
    public void load_corruptedSecondLine_reportsLineNumber() throws IOException {
        Path dataFile = temporaryDirectory.resolve("snoopy.txt");
        Files.writeString(dataFile, "T | 0 | valid task\nD | 0 | bad date | 2026-02-30\n");
        Storage storage = new Storage(dataFile);

        SnoopyException exception = assertThrows(SnoopyException.class, storage::load);

        assertEquals("The data file is corrupted at line 2.", exception.getMessage());
    }

    @Test
    public void load_completedTodoAndEvent_restoresCompletionStates() throws IOException, SnoopyException {
        Path dataFile = temporaryDirectory.resolve("completed.txt");
        Files.writeString(dataFile, "T | 1 | done todo\n"
                + "E | 1 | done event | 2026-09-01 | 2026-09-02\n");
        Storage storage = new Storage(dataFile);

        TaskList tasks = storage.load();

        assertEquals("[T][X] done todo", tasks.get(0).toString());
        assertEquals("[E][X] done event (from: Sep 01 2026 to: Sep 02 2026)",
                tasks.get(1).toString());
    }

    @Test
    public void load_invalidRecordShapes_rejectsEachRecord() {
        String[] invalidRecords = {
            "T | 0",
            "T | 2 | invalid status",
            "X | 0 | unknown type",
            "T | 0 | ",
            "T | 0 | extra | field",
            "D | 0 | missing date",
            "D | 0 |  | 2026-09-01",
            "D | 0 | invalid date | 2026-02-30",
            "E | 0 | meeting | 2026-09-01",
            "E | 0 |  | 2026-09-01 | 2026-09-02",
            "E | 0 | meeting |  | 2026-09-02",
            "E | 0 | meeting | 2026-09-01 | ",
            "E | 0 | meeting | invalid | 2026-09-02",
            "E | 0 | meeting | 2026-09-01 | invalid",
            "E | 0 | reversed | 2026-09-02 | 2026-09-01",
            "E | 0 | same day | 2026-09-01 | 2026-09-01"
        };

        for (int i = 0; i < invalidRecords.length; i++) {
            Path dataFile = temporaryDirectory.resolve("invalid-" + i + ".txt");
            try {
                Files.writeString(dataFile, invalidRecords[i]);
            } catch (IOException exception) {
                throw new AssertionError("Test setup could not write a temporary data file", exception);
            }
            Storage storage = new Storage(dataFile);

            SnoopyException exception = assertThrows(SnoopyException.class, storage::load,
                    "Expected record to be rejected: " + invalidRecords[i]);
            assertEquals("The data file is corrupted at line 1.", exception.getMessage());
        }
    }

    @Test
    public void save_invalidParent_throwsError() throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "content");
        Storage storage = new Storage(parentFile.resolve("snoopy.txt"));

        assertThrows(IOException.class, () -> storage.save(new TaskList()));
    }

    @Test
    public void constructor_nullPath_failsAssertion() {
        assertThrows(AssertionError.class, () -> new Storage(null));
    }

    @Test
    public void save_invalidTaskLists_failAssertions() {
        Storage storage = new Storage(temporaryDirectory.resolve("assertions.txt"));
        assertThrows(AssertionError.class, () -> storage.save(null));
        assertThrows(AssertionError.class, () ->
                new TaskList(Collections.singletonList(null)));
    }
}
