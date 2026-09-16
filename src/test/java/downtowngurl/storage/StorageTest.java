package downtowngurl.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import downtowngurl.exception.DowntownGurlException;
import downtowngurl.task.Deadline;
import downtowngurl.task.Event;
import downtowngurl.task.RecurrenceFrequency;
import downtowngurl.task.Task;
import downtowngurl.task.TaskList;
import downtowngurl.task.Todo;

/**
 * Tests loading and saving tasks through the storage file format.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    /**
     * Checks that loading from a missing task file returns an empty list.
     *
     * @throws DowntownGurlException If loading unexpectedly fails.
     */
    @Test
    public void loadTasks_missingTaskFile_returnsEmptyList() throws DowntownGurlException {
        Storage storage = new Storage(this.tempDir.resolve("data").resolve("tasks.txt"));

        ArrayList<Task> tasks = storage.loadTasks();

        assertTrue(tasks.isEmpty());
        assertTrue(storage.getCorruptedLineNumbers().isEmpty());
    }

    /**
     * Checks that valid modern storage lines are loaded into the expected task objects.
     *
     * @throws DowntownGurlException If loading unexpectedly fails.
     * @throws IOException If the test file cannot be created.
     */
    @Test
    public void loadTasks_validModernStorageLines_returnsExpectedTasks()
            throws DowntownGurlException, IOException {
        Path taskFilePath = createTaskFile(
                "T * Not done * read book",
                "D * Done * submit report * 2019-12-02T18:00:00",
                "E * Not done * meeting * 2019-12-02T18:00:00 * 2019-12-02T20:00:00",
                "D * Not done * pay rent * 2019-12-03T12:00:00 * WEEKLY",
                "E * Done * sync * 2019-12-04T09:00:00 * 2019-12-04T10:00:00 * WEEKLY");
        Storage storage = new Storage(taskFilePath);

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(5, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[D][X] submit report (by: 02 Dec 2019, Monday 18:00)", tasks.get(1).toString());
        assertEquals("[E][ ] meeting (from: 02 Dec 2019, Monday 18:00 to: 02 Dec 2019, Monday 20:00)",
                tasks.get(2).toString());
        assertEquals("[D][ ] pay rent (by: 03 Dec 2019, Tuesday 12:00) (repeats weekly)",
                tasks.get(3).toString());
        assertEquals("[E][X] sync (from: 04 Dec 2019, Wednesday 09:00 to: 04 Dec 2019, Wednesday 10:00)"
                + " (repeats weekly)", tasks.get(4).toString());
        assertTrue(storage.getCorruptedLineNumbers().isEmpty());
    }

    /**
     * Checks that escaped storage characters are restored when loading descriptions.
     *
     * @throws DowntownGurlException If loading unexpectedly fails.
     * @throws IOException If the test file cannot be created.
     */
    @Test
    public void loadTasks_escapedStorageField_returnsUnescapedDescription()
            throws DowntownGurlException, IOException {
        Path taskFilePath = createTaskFile("T * Not done * line one\\nline two \\* urgent \\\\ done");
        Storage storage = new Storage(taskFilePath);

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] line one\nline two * urgent \\ done", tasks.getFirst().toString());
    }

    /**
     * Checks that corrupted saved lines are skipped and reported by line number.
     *
     * @throws DowntownGurlException If loading unexpectedly fails.
     * @throws IOException If the test file cannot be created.
     */
    @Test
    public void loadTasks_corruptedLines_skipsInvalidLinesAndRecordsLineNumbers()
            throws DowntownGurlException, IOException {
        Path taskFilePath = createTaskFile(
                "T * Not done * read book",
                "bad saved line",
                "D * Later * submit report * 2019-12-02T18:00:00",
                "D * Not done * pay rent * 2019-12-03T12:00:00 * DAILY",
                "",
                "T * Done * write report");
        Storage storage = new Storage(taskFilePath);

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][X] write report", tasks.get(1).toString());
        assertEquals(List.of(2, 3, 4), storage.getCorruptedLineNumbers());
    }

    /**
     * Checks that saving tasks writes the modern storage format.
     *
     * @throws DowntownGurlException If saving unexpectedly fails.
     * @throws IOException If the saved file cannot be read.
     */
    @Test
    public void saveTasks_taskList_writesModernStorageLines() throws DowntownGurlException, IOException {
        Path taskFilePath = this.tempDir.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(taskFilePath);
        TaskList tasks = new TaskList();
        Todo todo = new Todo("line one\nline two * urgent \\ done");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();
        Event event = new Event("meeting", LocalDateTime.of(2019, 12, 2, 18, 0),
                LocalDateTime.of(2019, 12, 2, 20, 0));
        Deadline recurringDeadline = new Deadline("pay rent", LocalDateTime.of(2019, 12, 3, 12, 0));
        recurringDeadline.setRecurrenceFrequency(RecurrenceFrequency.WEEKLY);
        Event recurringEvent = new Event("sync", LocalDateTime.of(2019, 12, 4, 9, 0),
                LocalDateTime.of(2019, 12, 4, 10, 0));
        recurringEvent.setRecurrenceFrequency(RecurrenceFrequency.WEEKLY);
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);
        tasks.add(recurringDeadline);
        tasks.add(recurringEvent);

        storage.saveTasks(tasks);

        assertEquals(List.of(
                "T * Not done * line one\\nline two \\* urgent \\\\ done",
                "D * Done * submit report * 2019-12-02T18:00:00",
                "E * Not done * meeting * 2019-12-02T18:00:00 * 2019-12-02T20:00:00",
                "D * Not done * pay rent * 2019-12-03T12:00:00 * WEEKLY",
                "E * Not done * sync * 2019-12-04T09:00:00 * 2019-12-04T10:00:00 * WEEKLY"),
                Files.readAllLines(taskFilePath));
    }

    /**
     * Checks that saving also works when the task file path has no explicit parent directory.
     *
     * @throws DowntownGurlException If saving unexpectedly fails.
     * @throws IOException If the saved file cannot be read.
     */
    @Test
    public void saveTasks_fileWithoutParent_writesTaskFile() throws DowntownGurlException, IOException {
        Path taskFilePath = Path.of("storage-parentless-test.txt");
        Files.deleteIfExists(taskFilePath);
        Storage storage = new Storage(taskFilePath);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        try {
            storage.saveTasks(tasks);

            assertEquals(List.of("T * Not done * read book"), Files.readAllLines(taskFilePath));
        } finally {
            Files.deleteIfExists(taskFilePath);
        }
    }

    /**
     * Creates a temporary task file containing the given lines.
     *
     * @param lines Lines to write into the task file.
     * @return Path to the created task file.
     * @throws IOException If the test file cannot be written.
     */
    private Path createTaskFile(String... lines) throws IOException {
        Path taskFilePath = this.tempDir.resolve("tasks.txt");
        Files.write(taskFilePath, List.of(lines));
        return taskFilePath;
    }
}
