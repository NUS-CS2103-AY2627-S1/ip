package nova.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nova.task.Deadline;
import nova.task.Event;
import nova.task.TaskList;
import nova.task.Todo;

/** Tests task persistence and recovery behavior. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies that saving and loading preserves tasks and completion status. */
    @Test
    void saveAndLoad_roundTripPreservesTasksAndCompletionStatus() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("nested/nova.txt"));
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 8, 24)));
        tasks.add(new Event("meeting", "2026-08-24 09:00", "2026-08-24 10:00"));
        tasks.addTag(1, "#reading");
        tasks.addTag(1, "#urgent");
        tasks.markAsDone(1);

        storage.save(tasks);
        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][X] read book #reading #urgent", loadedTasks.get(0).toString());
        assertEquals("[D][ ] submit report (by: Aug 24 2026)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] meeting (from: 2026-08-24 09:00 to: 2026-08-24 10:00)",
                loadedTasks.get(2).toString());
    }

    /** Verifies that saving creates missing parent directories. */
    @Test
    void save_createsMissingParentDirectories() throws Exception {
        Path taskFile = temporaryDirectory.resolve("new/data/nova.txt");
        Storage storage = new Storage(taskFile);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("new task"));

        storage.save(tasks);

        assertTrue(Files.exists(taskFile));
        assertEquals("T | 0 | new task", Files.readString(taskFile).trim());
    }

    /** Verifies that malformed records are ignored during loading. */
    @Test
    void load_ignoresMalformedRecordsAndLoadsValidRecords() throws Exception {
        Path taskFile = temporaryDirectory.resolve("nova.txt");
        Files.writeString(taskFile, String.join(System.lineSeparator(),
                "T | 0 | valid task",
                "D | 0 | invalid date | 2026-02-30",
                "E | 0 | invalid range | 2026-08-24 10:00 | 2026-08-24 09:00",
                "not a task record"));
        Storage storage = new Storage(taskFile);

        TaskList loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("valid task", loadedTasks.get(0).getDescription());
    }

    /** Verifies that legacy records without tag fields remain loadable. */
    @Test
    void load_legacyRecordsWithoutTags_remainsBackwardCompatible() throws Exception {
        Path taskFile = temporaryDirectory.resolve("legacy.txt");
        Files.writeString(taskFile, String.join(System.lineSeparator(),
                "T | 0 | todo",
                "D | 0 | deadline | 2026-08-24",
                "E | 0 | event | 2026-08-24 09:00 | 2026-08-24 10:00"));
        Storage storage = new Storage(taskFile);

        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertTrue(loadedTasks.get(0).getTags().isEmpty());
        assertTrue(loadedTasks.get(1).getTags().isEmpty());
        assertTrue(loadedTasks.get(2).getTags().isEmpty());
    }

    /** Verifies that a relative task filename without a parent directory can be saved. */
    @Test
    void save_relativeFilenameWithoutParent_succeeds() throws Exception {
        Path taskFile = Path.of("nova-relative-test.txt");
        Storage storage = new Storage(taskFile);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("relative task"));

        try {
            storage.save(tasks);

            assertEquals("T | 0 | relative task", Files.readString(taskFile).trim());
        } finally {
            Files.deleteIfExists(taskFile);
            Files.deleteIfExists(Path.of("nova-relative-test.txt.tmp"));
        }
    }
}
