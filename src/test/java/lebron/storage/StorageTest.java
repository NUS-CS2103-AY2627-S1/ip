package lebron.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lebron.task.DateTime;
import lebron.task.Deadline;
import lebron.task.Event;
import lebron.task.RecurrencePeriod;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.task.Todo;

/**
 * Tests for {@link Storage}: the load/save round trip that keeps the task
 * list on disk between runs, plus the "missing file" and "corrupted file"
 * cases the loader must survive. Each test uses a fresh {@link TempDir} so
 * nothing touches the real {@code ./data/} folder.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    private Path dataFile() {
        return tempDir.resolve("data").resolve("lebron.txt");
    }

    private TaskList sampleTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Deadline deadline = new Deadline("submit report", DateTime.parse("2019-12-02 1800"));
        deadline.markAsDone();
        tasks.add(deadline);
        tasks.add(new Event("camp", DateTime.parse("2019-06-01"), DateTime.parse("2019-06-03")));
        return tasks;
    }

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() {
        List<Task> loaded = new Storage(dataFile()).load();
        assertEquals(0, loaded.size());
    }

    @Test
    public void save_missingParentFolder_isCreated() {
        Storage storage = new Storage(dataFile());
        storage.save(sampleTasks());
        assertTrue(Files.exists(dataFile()));
    }

    @Test
    public void saveThenLoad_roundTripsEveryTaskType() {
        Storage storage = new Storage(dataFile());
        storage.save(sampleTasks());

        // A separate Storage instance, as if the program had restarted.
        ArrayList<Task> loaded = new Storage(dataFile()).load();

        assertEquals(3, loaded.size());
        assertEquals("[ ] read book", loaded.get(0).toString());
        assertEquals("[D][X] submit report (by: Dec 02 2019 6:00pm)", loaded.get(1).toString());
        assertEquals("[E][ ] camp (from: Jun 01 2019 to: Jun 03 2019)", loaded.get(2).toString());
    }

    @Test
    public void save_calledAgain_overwritesPreviousContents() {
        Storage storage = new Storage(dataFile());
        storage.save(sampleTasks());

        TaskList shorter = new TaskList();
        shorter.add(new Todo("only one left"));
        storage.save(shorter);

        ArrayList<Task> loaded = new Storage(dataFile()).load();
        assertEquals(1, loaded.size());
        assertEquals("[ ] only one left", loaded.get(0).toString());
    }

    @Test
    public void load_corruptedLine_isSkippedAndValidLinesKept() throws IOException {
        Files.createDirectories(dataFile().getParent());
        Files.write(dataFile(), List.of(
                "T | 0 | read book",
                "this line is not a task",
                "D | 1 | submit report | 2019-12-02 1800"));

        ArrayList<Task> loaded = new Storage(dataFile()).load();

        assertEquals(2, loaded.size());
        assertEquals("[ ] read book", loaded.get(0).toString());
        assertEquals("[D][X] submit report (by: Dec 02 2019 6:00pm)", loaded.get(1).toString());
    }

    @Test
    public void load_blankLines_ignored() throws IOException {
        Files.createDirectories(dataFile().getParent());
        Files.write(dataFile(), List.of("", "T | 0 | read book", "   ", ""));

        ArrayList<Task> loaded = new Storage(dataFile()).load();

        assertEquals(1, loaded.size());
    }

    // ---- recurrence (B-RecurringTasks) -----------------------------------

    @Test
    public void saveThenLoad_recurringDeadline_preservesPeriod() {
        Storage storage = new Storage(dataFile());
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("pay rent", DateTime.parse("2025-01-01"), RecurrencePeriod.MONTH));
        storage.save(tasks);

        ArrayList<Task> loaded = new Storage(dataFile()).load();

        assertEquals(1, loaded.size());
        assertEquals("[D][ ] pay rent (by: Jan 01 2025) (every: month)", loaded.get(0).toString());
    }

    @Test
    public void load_oldFormatFileWithoutRecurrenceField_stillLoadsAsNonRecurring() throws IOException {
        // Simulates a data file saved before B-RecurringTasks existed: no
        // trailing period field on the D/E lines.
        Files.createDirectories(dataFile().getParent());
        Files.write(dataFile(), List.of(
                "D | 0 | old deadline | 2019-12-02",
                "E | 0 | old event | 2019-06-01 | 2019-06-03"));

        ArrayList<Task> loaded = new Storage(dataFile()).load();

        assertEquals(2, loaded.size());
        assertEquals("[D][ ] old deadline (by: Dec 02 2019)", loaded.get(0).toString());
        assertEquals("[E][ ] old event (from: Jun 01 2019 to: Jun 03 2019)", loaded.get(1).toString());
    }

    @Test
    public void load_corruptedRecurrencePeriod_isSkippedAndValidLinesKept() throws IOException {
        Files.createDirectories(dataFile().getParent());
        Files.write(dataFile(), List.of(
                "D | 0 | good deadline | 2025-01-01 | month",
                "D | 0 | bad recurrence | 2025-01-01 | fortnight"));

        ArrayList<Task> loaded = new Storage(dataFile()).load();

        assertEquals(1, loaded.size());
        assertEquals("[D][ ] good deadline (by: Jan 01 2025) (every: month)", loaded.get(0).toString());
    }
}
