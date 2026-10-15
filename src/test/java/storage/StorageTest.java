package storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import exception.CbtException;
import task.Deadline;
import task.Event;
import task.TaskList;
import task.Todo;

/** Tests persistence of task lists without touching the real application data. */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies that loading a missing file returns an empty task list. */
    @Test
    public void loadTasks_missingFile_emptyListReturned() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertEquals(0, storage.loadTasks().getSize());
        assertTrue(storage.getLoadWarning().isEmpty());
    }

    /** Verifies that a directory used as the data path produces a readable startup warning. */
    @Test
    public void loadTasks_pathIsDirectory_emptyListAndWarningReturned() {
        Storage storage = new Storage(temporaryDirectory.toString());

        assertEquals(0, storage.loadTasks().getSize());
        assertTrue(storage.getLoadWarning().orElseThrow().contains("not a regular file"));
    }

    /** Verifies that multiple task types and completion states survive a save-load cycle. */
    @Test
    public void saveThenLoadTasks_multipleTaskTypes_contentAndStatusRestored()
            throws CbtException, IOException {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        TaskList original = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.addTask(todo);
        original.addTask(new Deadline("submit work",
                LocalDateTime.of(2026, 8, 26, 18, 0)));
        original.addTask(new Event("conference",
                LocalDateTime.of(2026, 8, 27, 9, 0),
                LocalDateTime.of(2026, 8, 27, 17, 0)));

        storage.saveTasks(original);
        TaskList loaded = storage.loadTasks();

        assertTrue(Files.exists(dataFile));
        assertEquals(3, loaded.getSize());
        assertEquals("TODO | 1 | read book", loaded.getTask(0).toFileFormat());
        assertEquals("DEADLINE | 0 | submit work | 26/08/2026 1800",
                loaded.getTask(1).toFileFormat());
        assertEquals("EVENT | 0 | conference | 27/08/2026 0900 | 27/08/2026 1700",
                loaded.getTask(2).toFileFormat());
    }

    /** Verifies that saving replaces existing file contents. */
    @Test
    public void saveTasks_existingFile_previousContentReplaced() throws IOException, CbtException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "old content");
        Storage storage = new Storage(dataFile.toString());
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("new task"));

        storage.saveTasks(tasks);
        String content = Files.readString(dataFile);

        assertTrue(content.contains("TODO | 0 | new task"));
        assertFalse(content.contains("old content"));
    }

    /** Verifies saving an empty list creates an empty data file and replaces old content. */
    @Test
    public void saveTasks_emptyList_emptyFileCreated() throws IOException, CbtException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "stale task");

        new Storage(dataFile.toString()).saveTasks(new TaskList());

        assertTrue(Files.exists(dataFile));
        assertEquals("", Files.readString(dataFile));
    }

    /** Verifies that malformed stored lines are skipped while valid lines are loaded. */
    @Test
    public void loadTasks_malformedLine_validLinesStillLoaded() throws IOException, CbtException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "broken line" + System.lineSeparator()
                + "TODO | 0 | valid task" + System.lineSeparator());

        Storage storage = new Storage(dataFile.toString());
        TaskList loaded = storage.loadTasks();

        assertEquals(1, loaded.getSize());
        assertEquals("[T][ ] valid task", loaded.getTask(0).toString());
        assertTrue(storage.getLoadWarning().orElseThrow().contains("1 malformed or duplicate"));
    }

    /** Verifies that malformed and duplicate saved tasks are skipped with a warning. */
    @Test
    public void loadTasks_invalidAndDuplicateRecords_validTaskLoadedAndWarningReturned()
            throws IOException, CbtException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "TODO | invalid | bad status" + System.lineSeparator()
                + "TODO | 0 | read book" + System.lineSeparator()
                + "TODO | 1 | READ BOOK" + System.lineSeparator());
        Storage storage = new Storage(dataFile.toString());

        TaskList loaded = storage.loadTasks();

        assertEquals(1, loaded.getSize());
        assertEquals("[T][ ] read book", loaded.getTask(0).toString());
        assertTrue(storage.getLoadWarning().orElseThrow().contains("2 malformed or duplicate"));
    }

    /** Verifies blank lines are ignored and a later clean load clears an earlier warning. */
    @Test
    public void loadTasks_blankLinesThenCleanReload_warningAbsent() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "broken" + System.lineSeparator());
        Storage storage = new Storage(dataFile.toString());
        storage.loadTasks();
        assertTrue(storage.getLoadWarning().isPresent());

        Files.writeString(dataFile, System.lineSeparator() + "TODO | 0 | valid" + System.lineSeparator());
        TaskList loaded = storage.loadTasks();

        assertEquals(1, loaded.getSize());
        assertTrue(storage.getLoadWarning().isEmpty());
    }

    /** Verifies that a destination that cannot be replaced produces a user-facing error. */
    @Test
    public void saveTasks_destinationIsDirectory_exceptionThrown() {
        Storage storage = new Storage(temporaryDirectory.toString());
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        assertThrows(CbtException.class, () -> storage.saveTasks(tasks));
        assertTrue(Files.isDirectory(temporaryDirectory));
    }

    /** Verifies that chronological task order survives a save-load cycle. */
    @Test
    public void saveThenLoadTasks_sortedTasks_sortedOrderRestored() throws CbtException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("undated"));
        tasks.addTask(new Deadline("later", LocalDateTime.of(2026, 8, 28, 12, 0)));
        tasks.addTask(new Deadline("earlier", LocalDateTime.of(2026, 8, 27, 12, 0)));
        tasks.sortChronologically();

        storage.saveTasks(tasks);
        TaskList loaded = storage.loadTasks();

        assertEquals("DEADLINE | 0 | earlier | 27/08/2026 1200", loaded.getTask(0).toFileFormat());
        assertEquals("DEADLINE | 0 | later | 28/08/2026 1200", loaded.getTask(1).toFileFormat());
        assertEquals("TODO | 0 | undated", loaded.getTask(2).toFileFormat());
    }
}
