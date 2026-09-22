package oz.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import oz.exception.OzException;
import oz.task.Deadline;
import oz.task.Event;
import oz.task.RecurringEvent;
import oz.task.Task;
import oz.task.TaskDateTime;
import oz.task.TaskList;
import oz.task.ToDo;

/**
 * Tests storage compatibility, task round trips, and recovery from corrupted entries.
 */
public class StorageTest {
    /** Temporary directory isolating each test's storage files. */
    @TempDir
    Path temporaryFolder;

    /** Verifies that starting without a storage file produces an empty list. */
    @Test
    public void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(this.temporaryFolder.resolve("missing.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    /** Verifies all task types and completion states survive saving and loading. */
    @Test
    public void save_allTaskTypes_roundTripsFieldsAndStatus() throws OzException, IOException {
        Path storageFile = this.temporaryFolder.resolve("nested/tasks.txt");
        Storage storage = new Storage(storageFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return book", TaskDateTime.parse("2026-10-10")));
        tasks.add(new Event("meeting", TaskDateTime.parse("2026-10-10 1400"),
                TaskDateTime.parse("2026-10-10 1630")));

        for (boolean isDone : new boolean[] {false, true}) {
            if (isDone) {
                for (Task task : tasks.getTasks()) {
                    task.markAsDone();
                }
            }
            storage.save(tasks.getTasks());
            String expectedStatus = isDone ? "1" : "0";
            assertEquals(List.of(
                    "T | " + expectedStatus + " | read book",
                    "D | " + expectedStatus + " | return book | 2026-10-10",
                    "E | " + expectedStatus + " | meeting | 2026-10-10 1400 | 2026-10-10 1630"),
                    Files.readAllLines(storageFile));
            ArrayList<Task> loaded = storage.load();
            assertEquals(tasks.size(), loaded.size());
            for (int i = 0; i < tasks.size(); i++) {
                assertEquals(tasks.get(i).toFileFormat(), loaded.get(i).toFileFormat());
                assertEquals(tasks.get(i).toString(), loaded.get(i).toString());
            }
        }
        storage.save(new TaskList().getTasks());
        assertTrue(Files.readAllLines(storageFile).isEmpty());
    }

    /** Verifies corrupted entries do not prevent later valid records from loading. */
    @Test
    public void load_corruptedEntries_skipsInvalidRecords() throws IOException {
        Path storageFile = this.temporaryFolder.resolve("tasks.txt");
        Files.write(storageFile, List.of(
                "T | 0 | first", "broken", "T | 2 | invalid status", "X | 0 | unknown type",
                "T | 0 | ", "D | 0 | missing date", "D | 0 | | 2026-10-10",
                "D | 0 | missing date | ", "D | 0 | bad date | invalid",
                "E | 0 | missing end | 2026-10-10", "E | 0 | | 2026-10-10 | 2026-10-11",
                "E | 0 | missing start | | 2026-10-11", "E | 0 | missing end | 2026-10-10 | ",
                "E | 0 | bad date | invalid | 2026-10-11",
                "E | 0 | reversed | 2026-10-11 | 2026-10-10", "T | 1 | last"));

        ArrayList<Task> loaded = new Storage(storageFile.toString()).load();
        assertEquals(2, loaded.size());
        assertEquals("T | 0 | first", loaded.get(0).toFileFormat());
        assertEquals("T | 1 | last", loaded.get(1).toFileFormat());
    }

    /** Verifies whitespace and byte-order marks do not become task data. */
    @Test
    public void load_blankLinesAndByteOrderMark_loadsTrimmedTasks() throws IOException {
        Path storageFile = this.temporaryFolder.resolve("tasks.txt");
        Files.writeString(storageFile, "\uFEFF T | 0 | read book  \n\n  \n\uFEFF\n T|1|return book \n");
        ArrayList<Task> loaded = new Storage(storageFile.toString()).load();
        assertEquals(2, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(0).toFileFormat());
        assertEquals("T | 1 | return book", loaded.get(1).toFileFormat());
    }

    /** Verifies that the existing todo format permits pipes inside descriptions. */
    @Test
    public void load_todoDescriptionContainingPipe_preservesDescription() throws IOException {
        Path storageFile = this.temporaryFolder.resolve("tasks.txt");
        Files.writeString(storageFile, "T | 0 | choose tea | coffee\n");
        ArrayList<Task> loaded = new Storage(storageFile.toString()).load();
        assertEquals(1, loaded.size());
        assertEquals("T | 0 | choose tea | coffee", loaded.get(0).toFileFormat());
    }

    @Test
    public void save_recurringEvent_roundTripsRuleAndCompletedOccurrences()
            throws OzException, IOException {
        Path storageFile = this.temporaryFolder.resolve("recurring.txt");
        Storage storage = new Storage(storageFile.toString());
        TaskList tasks = new TaskList();
        RecurringEvent recurringEvent = new RecurringEvent("project meeting",
                TaskDateTime.parse("2026-10-02 1400"),
                TaskDateTime.parse("2026-10-02 1500"), 2,
                TaskDateTime.parseDate("2026-12-31"));
        recurringEvent.markOccurrence(TaskDateTime.parseDate("2026-10-16"));
        tasks.add(recurringEvent);

        storage.save(tasks.getTasks());

        String expected = "R | 0 | project meeting | 2026-10-02 1400 | "
                + "2026-10-02 1500 | 2 | 2026-12-31 | 2026-10-16";
        assertEquals(List.of(expected), Files.readAllLines(storageFile));
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals(expected, loaded.get(0).toFileFormat());
        assertEquals(recurringEvent.toString(), loaded.get(0).toString());
    }

    @Test
    public void save_indefiniteRecurringEvent_roundTripsEmptyOptionalFields()
            throws OzException, IOException {
        Path storageFile = this.temporaryFolder.resolve("indefinite.txt");
        Storage storage = new Storage(storageFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new RecurringEvent("weekly meeting",
                TaskDateTime.parse("2026-10-02 1400"),
                TaskDateTime.parse("2026-10-02 1500"), 1, null));

        storage.save(tasks.getTasks());

        String expected = "R | 0 | weekly meeting | 2026-10-02 1400 | "
                + "2026-10-02 1500 | 1 | - | -";
        assertEquals(List.of(expected), Files.readAllLines(storageFile));
        assertEquals(expected, storage.load().get(0).toFileFormat());
    }

    @Test
    public void load_corruptedRecurringEntries_skipsInvalidRecords() throws IOException {
        Path storageFile = this.temporaryFolder.resolve("recurring.txt");
        Files.write(storageFile, List.of(
                "R | 1 | meeting | 2026-10-02 1400 | 2026-10-02 1500 | 1 | - | -",
                "R | 0 | meeting | 2026-10-02 1400 | 2026-10-03 1500 | 1 | - | -",
                "R | 0 | meeting | 2026-10-02 1400 | 2026-10-02 1500 | 0 | - | -",
                "R | 0 | meeting | 2026-10-02 1400 | 2026-10-02 1500 | 1 | - | 2026-10-03",
                "T | 0 | valid"));

        ArrayList<Task> loaded = new Storage(storageFile.toString()).load();
        assertEquals(1, loaded.size());
        assertEquals("T | 0 | valid", loaded.get(0).toFileFormat());
    }

    @Test
    public void save_readOnlyFile_throwsOzException() throws IOException {
        Path storageFile = this.temporaryFolder.resolve("readonly.txt");
        Files.createFile(storageFile);
        storageFile.toFile().setReadOnly();

        Storage storage = new Storage(storageFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("test task"));

        OzException exception = org.junit.jupiter.api.Assertions.assertThrows(
                OzException.class, () -> storage.save(tasks.getTasks()));
        assertTrue(exception.getMessage().contains("read-only")
                || exception.getMessage().contains("Access denied")
                || exception.getMessage().contains("permission"));

        storageFile.toFile().setWritable(true);
    }

    @Test
    public void load_insufficientAndEmptyFieldsRecurring_skipsInvalidRecords() throws IOException {
        Path storageFile = this.temporaryFolder.resolve("corrupted_recurring.txt");
        Files.write(storageFile, List.of(
                "R | 0 | insufficient fields | 2026-10-02 1400 | 2026-10-02 1500 | 1 | -",
                "R | 0 | | 2026-10-02 1400 | 2026-10-02 1500 | 1 | - | -",
                "R | 0 | empty start | | 2026-10-02 1500 | 1 | - | -",
                "R | 0 | empty end | 2026-10-02 1400 | | 1 | - | -",
                "R | 0 | non numeric interval | 2026-10-02 1400 | 2026-10-02 1500 | abc | - | -",
                "R | 0 | interval overflow | 2026-10-02 1400 | 2026-10-02 1500 | 999999999999999 | - | -",
                "T | 0 | surviving task"));

        ArrayList<Task> loaded = new Storage(storageFile.toString()).load();
        assertEquals(1, loaded.size());
        assertEquals("T | 0 | surviving task", loaded.get(0).toFileFormat());
    }

    @Test
    public void save_listImplementation_persistsTasksSuccessfully() throws OzException, IOException {
        Path storageFile = this.temporaryFolder.resolve("list_save.txt");
        Storage storage = new Storage(storageFile.toString());

        storage.save(List.of(new ToDo("test todo")));
        assertEquals(List.of("T | 0 | test todo"), Files.readAllLines(storageFile));
    }
}
