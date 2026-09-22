package mono.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mono.task.Deadline;
import mono.task.Event;
import mono.task.Task;
import mono.task.ToDo;
import mono.testutil.StorageTestSupport;

/**
 * Tests saving and loading task records from isolated test storage.
 */
public class StorageTest {
    private static final Path STORAGE_FILE = Path.of("data", "tasks.txt");

    @BeforeEach
    public void resetStorage() throws IOException {
        StorageTestSupport.resetStorage();
    }

    @Test
    public void saveTasks_variedTasks_readStorageReturnsEquivalentTasks() {
        ToDo todo = new ToDo("read a book");
        Deadline deadline = new Deadline("submit report", "2026-09-01");
        Event event = new Event("project retreat", "2026-09-02", "2026-09-03");
        deadline.markDone();
        Storage storage = new Storage();

        storage.saveTasks(new Task[]{todo, deadline, event});

        assertArrayEquals(
                new String[]{
                    "T | 0 | read a book",
                    "D | 1 | submit report | 2026-09-01",
                    "E | 0 | project retreat | 2026-09-02 | 2026-09-03"
                },
                toFileStrings(storage.readStorage()));
    }

    @Test
    public void saveTasks_existingTasks_replacesStoredTasks() {
        Storage storage = new Storage();
        storage.saveTasks(new Task[]{new ToDo("old task")});

        storage.saveTasks(new Task[]{new ToDo("replacement task")});

        assertArrayEquals(
                new String[]{"T | 0 | replacement task"},
                toFileStrings(storage.readStorage()));
    }

    @Test
    public void readStorage_blankAndMalformedRecords_returnsOnlyValidTasks() throws IOException {
        new Storage();
        Files.writeString(
                STORAGE_FILE,
                String.join(
                        System.lineSeparator(),
                        "",
                        "T | 0 | valid task",
                        "T | 2 | invalid status",
                        "D | 0 | missing date",
                        "E | 1 | event | 2026-09-02 | 2026-09-03",
                        "X | 0 | unknown task type",
                        "malformed"));

        Task[] tasks = new Storage().readStorage();

        assertArrayEquals(
                new String[]{
                    "T | 0 | valid task",
                    "E | 1 | event | 2026-09-02 | 2026-09-03"
                },
                toFileStrings(tasks));
    }

    @Test
    public void readStorage_recordsWithInvalidDatesAndMissingFields_skipsInvalidLines()
            throws IOException {
        new Storage();
        Files.writeString(STORAGE_FILE, String.join(System.lineSeparator(),
                "D | 0 | invalid date | 2026-02-30",
                "E | 0 | missing end | 2026-09-01",
                "E | 0 | invalid range | not-a-date | 2026-09-03",
                "T | 0 | valid task"));

        assertEquals("T | 0 | valid task", new Storage().readStorage()[0].toFileString());
        assertEquals(1, new Storage().readStorage().length);
    }

    @Test
    public void saveTasks_emptyArray_clearsExistingStorage() {
        Storage storage = new Storage();
        storage.saveTasks(new Task[]{new ToDo("old task")});

        storage.saveTasks(new Task[0]);

        assertEquals(0, storage.readStorage().length);
    }

    private String[] toFileStrings(Task[] tasks) {
        return Arrays.stream(tasks).map(Task::toFileString).toArray(String[]::new);
    }
}
