package marcus.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import marcus.task.Deadline;
import marcus.task.Event;
import marcus.task.Task;
import marcus.task.TaskList;
import marcus.task.Todo;
import marcus.ui.Ui;

/** Tests loading and saving task records. */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that all supported task types and completion states round-trip through storage. */
    @Test
    void saveAndLoad_supportedTasks_restoresRecords() {
        Path savePath = temporaryDirectory.resolve("data/results.txt");
        Storage storage = new Storage(savePath.toString());
        TaskList originalTasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 15));
        Event event = new Event("team meeting", "Monday", "Tuesday");
        deadline.markAsDone();
        originalTasks.add(todo);
        originalTasks.add(deadline);
        originalTasks.add(event);

        assertTrue(storage.save(originalTasks, new Ui()));
        Task[] loadedTasks = storage.load(new Ui());

        assertEquals("T | 0 | read book", loadedTasks[0].toFileString());
        assertEquals("D | 1 | submit report | 2026-09-15", loadedTasks[1].toFileString());
        assertEquals("E | 0 | team meeting | Monday | Tuesday", loadedTasks[2].toFileString());
    }

    /** Verifies that malformed records and duplicate records are skipped during loading. */
    @Test
    void load_malformedAndDuplicateRecords_keepsOnlyValidUniqueTasks() throws IOException {
        Path savePath = temporaryDirectory.resolve("results.txt");
        Files.writeString(savePath, "T | 0 | read book\n"
                + "T | 0 | read book\n"
                + "D | 0 | invalid | 2026-02-30\n"
                + "E | 0 | backwards | 2026-09-15 16:00 | 2026-09-15 15:00\n"
                + "T | 0 | write notes\n");

        Task[] loadedTasks = new Storage(savePath.toString()).load(new Ui());

        assertEquals("T | 0 | read book", loadedTasks[0].toFileString());
        assertEquals("T | 0 | write notes", loadedTasks[1].toFileString());
        assertNull(loadedTasks[2]);
    }

    /** Verifies that a missing data file is treated as an empty task list. */
    @Test
    void load_missingFile_returnsEmptyTaskArray() {
        Path savePath = temporaryDirectory.resolve("missing.txt");

        Task[] loadedTasks = new Storage(savePath.toString()).load(new Ui());

        assertNull(loadedTasks[0]);
    }
}
