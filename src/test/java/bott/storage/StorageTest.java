package bott.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bott.BottException;
import bott.task.Deadline;
import bott.task.Event;
import bott.task.FixedDurationTask;
import bott.task.Task;
import bott.task.Todo;

/**
 * Tests {@link Storage}. {@code parseSavedTask} is private and has no test of its own; its
 * behavior (valid lines of each task type, and corrupted lines) is exercised entirely through
 * {@code load()} below, since that is its only caller.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("does-not-exist.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void save_thenLoad_roundTripsAllTaskTypesAndStatuses() throws BottException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        List<Task> original = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));
        Event event = new Event("trip", LocalDate.of(2019, 8, 4), LocalDate.of(2019, 8, 5));
        event.markAsDone();
        original.add(event);
        original.add(new FixedDurationTask("read sales report", 90));

        storage.save(original);
        List<Task> loaded = storage.load();

        assertEquals(4, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][X] trip (from: Aug 04 2019 to: Aug 05 2019)", loaded.get(2).toString());
        assertEquals("[F][ ] read sales report (for: 1h 30m)", loaded.get(3).toString());
    }

    @Test
    public void save_parentDirectoryDoesNotExist_createsIt() throws BottException {
        Path filePath = tempDir.resolve("nested/subdir/tasks.txt");
        Storage storage = new Storage(filePath.toString());

        storage.save(List.of(new Todo("read book")));

        assertTrue(Files.exists(filePath));
    }

    @Test
    public void save_pathIsUnwritable_bottExceptionThrown() throws IOException {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.createDirectory(filePath);
        Storage storage = new Storage(filePath.toString());

        assertThrows(BottException.class, () -> storage.save(List.of(new Todo("read book"))));
    }

    @Test
    public void load_corruptedLinesInterspersedWithValidOnes_skipsOnlyCorruptedLines() throws IOException {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(filePath, String.join(System.lineSeparator(),
                "T | 1 | read book",
                "this line has no delimiters at all",
                "D | 0 | return book | 2019-10-15",
                "X | 0 | unknown task type",
                "D | 0 | missing by field",
                "E | 0 | trip | 2019-08-04 | 2019-08-05",
                "F | 1 | read sales report | 120",
                "F | 0 | broken duration | not-a-number",
                ""));
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertEquals(4, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] trip (from: Aug 04 2019 to: Aug 05 2019)", loaded.get(2).toString());
        assertEquals("[F][X] read sales report (for: 2h)", loaded.get(3).toString());
    }

    @Test
    public void load_emptyFile_returnsEmptyList() throws IOException {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(filePath, "");
        Storage storage = new Storage(filePath.toString());

        assertTrue(storage.load().isEmpty());
    }
}
