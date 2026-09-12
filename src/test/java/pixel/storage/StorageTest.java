package pixel.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pixel.task.Deadline;
import pixel.task.Event;
import pixel.task.Task;
import pixel.task.TaskList;
import pixel.task.Todo;

/** Tests persistence, missing-file handling, and recovery of invalid records. */
class StorageTest {

    @Test
    void saveAndLoad_typedTasks_preservesOrderAndCompletionState(@TempDir Path tempDir)
            throws Exception {
        Path file = tempDir.resolve("nested").resolve("pixel.txt");
        Storage storage = new Storage(file);
        TaskList original = new TaskList();
        original.add(new Todo("read book"));
        Deadline deadline = new Deadline("submit report", java.time.LocalDate.of(2019, 10, 15));
        deadline.markAsDone();
        original.add(deadline);
        original.add(new Event("meeting", "2pm", "4pm"));

        storage.save(original);
        ArrayList<String> warnings = new ArrayList<>();
        ArrayList<Task> loaded = storage.load(warnings);

        assertTrue(Files.exists(file));
        assertTrue(warnings.isEmpty());
        assertEquals(3, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[D][X] submit report (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] meeting (from: 2pm to: 4pm)", loaded.get(2).toString());
    }

    @Test
    void load_missingFile_returnsEmptyTasksWithoutWarnings(@TempDir Path tempDir) {
        Storage storage = new Storage(tempDir.resolve("missing.txt"));
        ArrayList<String> warnings = new ArrayList<>();

        ArrayList<Task> loaded = storage.load(warnings);

        assertTrue(loaded.isEmpty());
        assertTrue(warnings.isEmpty());
    }

    @Test
    void load_pathIsDirectory_returnsEmptyTasksWithWarning(@TempDir Path tempDir) {
        Storage storage = new Storage(tempDir);
        ArrayList<String> warnings = new ArrayList<>();

        ArrayList<Task> loaded = storage.load(warnings);

        assertTrue(loaded.isEmpty());
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("couldn't read the saved tasks"));
    }

    @Test
    void load_mixedValidAndInvalidRecords_recoversValidTasksAndReportsWarnings(@TempDir Path tempDir)
            throws Exception {
        Path file = tempDir.resolve("pixel.txt");
        Files.writeString(file, "T | 0 | valid\n"
                + "D | 0 | invalid | not-a-date\n"
                + "E | 1 | meeting | 2pm | 4pm\n");
        Storage storage = new Storage(file);
        ArrayList<String> warnings = new ArrayList<>();

        ArrayList<Task> loaded = storage.load(warnings);

        assertEquals(2, loaded.size());
        assertEquals("[T][ ] valid", loaded.get(0).toString());
        assertEquals("[E][X] meeting (from: 2pm to: 4pm)", loaded.get(1).toString());
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("line 2"));
    }

    @Test
    void load_duplicateAndInvalidEventRecords_skipsBadRecordsWithWarnings(@TempDir Path tempDir)
            throws Exception {
        Path file = tempDir.resolve("pixel.txt");
        Files.writeString(file, "T | 0 | repeated\n"
                + "T | 1 | repeated\n"
                + "E | 0 | backwards | 2026-09-13 | 2026-09-12\n"
                + "T | 0 | valid\n");
        Storage storage = new Storage(file);
        ArrayList<String> warnings = new ArrayList<>();

        ArrayList<Task> loaded = storage.load(warnings);

        assertEquals(2, loaded.size());
        assertEquals("[T][ ] repeated", loaded.get(0).toString());
        assertEquals("[T][ ] valid", loaded.get(1).toString());
        assertEquals(2, warnings.size());
        assertTrue(warnings.get(0).contains("duplicate saved task on line 2"));
        assertTrue(warnings.get(1).contains("event start must be before"));
    }
}
