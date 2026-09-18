package toothless.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import toothless.DisplaySymbols;
import toothless.task.Deadline;
import toothless.task.Event;
import toothless.task.TaskList;
import toothless.task.Todo;

/**
 * Tests saving and loading task data through isolated temporary files.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies the saved format for every task type and both completion states.
     */
    @Test
    public void save_mixedTaskList_writesEveryTypeFieldAndCompletionStatus() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read | chapter \\ one");
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        Event event = new Event("project meeting", "Monday 2pm", "Monday 3pm");
        deadline.markAsDone();
        tasks.addTask(todo);
        tasks.addTask(deadline);
        tasks.addTask(event);

        storage.save(tasks);

        List<String> savedLines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        assertEquals(List.of(
                "T | 0 | read \\| chapter \\\\ one",
                "D | 1 | return book | 2019-12-02",
                "E | 0 | project meeting | Monday 2pm | Monday 3pm"), savedLines);
        assertTrue(deadline.toString().contains(DisplaySymbols.getDoneMark()));
        assertFalse(savedLines.get(1).contains(DisplaySymbols.getDoneMark()));
    }

    /**
     * Verifies loading restores concrete task types, values, and completion states.
     */
    @Test
    public void load_mixedSavedData_restoresEveryTypeFieldAndCompletionStatus()
            throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of(
                "T | 0 | read \\| chapter \\\\ one",
                "D | 1 | return book | 2019-12-02",
                "E | 1 | project meeting | Monday 2pm | Monday 3pm"),
                StandardCharsets.UTF_8);

        StorageLoadResult result = new Storage(dataFile).load();

        TaskList loadedTasks = result.getTaskList();
        assertEquals(0, result.getMalformedLineCount());
        assertEquals(3, loadedTasks.size());
        Todo todo = assertInstanceOf(Todo.class, loadedTasks.getTask(0));
        Deadline deadline = assertInstanceOf(Deadline.class, loadedTasks.getTask(1));
        Event event = assertInstanceOf(Event.class, loadedTasks.getTask(2));
        assertEquals("read | chapter \\ one", todo.getDescription());
        assertFalse(todo.isDone());
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertTrue(deadline.isDone());
        assertEquals("project meeting", event.getDescription());
        assertEquals("Monday 2pm", event.getFrom());
        assertEquals("Monday 3pm", event.getTo());
        assertTrue(event.isDone());
    }

    /**
     * Verifies saving and loading a mixed list preserves order and display behavior.
     */
    @Test
    public void saveThenLoad_mixedTaskList_roundTripsEveryTask() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        TaskList originalTasks = new TaskList();
        originalTasks.addTask(new Todo("borrow book"));
        originalTasks.addTask(new Deadline("return book", LocalDate.of(2019, 12, 6)));
        originalTasks.addTask(new Event("team meeting", "Tuesday 2pm", "Tuesday 3pm"));
        originalTasks.markTask(1);
        originalTasks.markTask(3);

        storage.save(originalTasks);
        TaskList loadedTasks = storage.load().getTaskList();

        assertEquals(originalTasks.size(), loadedTasks.size());
        for (int i = 0; i < originalTasks.size(); i++) {
            assertEquals(originalTasks.getTask(i).toString(), loadedTasks.getTask(i).toString(),
                    "task " + (i + 1) + " should survive the round trip");
        }
    }

    /**
     * Verifies escaped boundary characters remain reversible in task descriptions.
     */
    @Test
    public void saveThenLoad_fieldsWithBoundaryCharacters_restoresExactText() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        TaskList originalTasks = new TaskList();
        originalTasks.addTask(new Todo("pipe | slash \\ newline\nreturn\r"));
        originalTasks.addTask(new Event("event | \\ title", "2pm", "3pm"));

        storage.save(originalTasks);
        TaskList loadedTasks = storage.load().getTaskList();

        Todo todo = assertInstanceOf(Todo.class, loadedTasks.getTask(0));
        Event event = assertInstanceOf(Event.class, loadedTasks.getTask(1));
        assertEquals("pipe | slash \\ newline\nreturn\r", todo.getDescription());
        assertEquals("event | \\ title", event.getDescription());
        assertEquals("2pm", event.getFrom());
        assertEquals("3pm", event.getTo());
    }

    /**
     * Verifies loading from a missing data directory returns an empty list without creating it.
     */
    @Test
    public void load_missingDataDirectory_returnsEmptyListWithoutCreatingDirectory()
            throws Exception {
        Path dataFile = temporaryDirectory.resolve("missing").resolve("tasks.txt");

        StorageLoadResult result = new Storage(dataFile).load();

        assertTrue(result.getTaskList().isEmpty());
        assertEquals(0, result.getMalformedLineCount());
        assertTrue(Files.notExists(dataFile.getParent()));
    }

    /**
     * Verifies loading from a missing file returns an empty list without creating the file.
     */
    @Test
    public void load_missingDataFile_returnsEmptyListWithoutCreatingFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");

        StorageLoadResult result = new Storage(dataFile).load();

        assertTrue(result.getTaskList().isEmpty());
        assertEquals(0, result.getMalformedLineCount());
        assertTrue(Files.notExists(dataFile));
    }

    /**
     * Verifies the first save creates missing parent directories and a readable data file.
     */
    @Test
    public void save_missingParentDirectory_createsPathsAndReadableFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first saved task"));

        storage.save(tasks);

        assertTrue(Files.isDirectory(dataFile.getParent()));
        assertTrue(Files.isRegularFile(dataFile));
        assertEquals("[T][ ] first saved task",
                storage.load().getTaskList().getTask(0).toString());
    }

    /**
     * Verifies data files with short names can still use safe temporary replacement files.
     */
    @Test
    public void save_shortDataFileName_replacesFileSuccessfully() throws Exception {
        Path dataFile = temporaryDirectory.resolve("x");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("short file name"));

        storage.save(tasks);

        assertEquals(List.of("T | 0 | short file name"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
        assertEquals("short file name",
                storage.load().getTaskList().getTask(0).getDescription());
    }

    /**
     * Verifies expected file-system failures become storage errors without corrupting state.
     */
    @Test
    public void loadAndSave_expectedIoFailures_throwStorageExceptionAndPreserveState()
            throws Exception {
        Path directoryAsFile = Files.createDirectory(
                temporaryDirectory.resolve("directory-as-file"));
        StorageException loadException = assertThrows(
                StorageException.class, () ->
                new Storage(directoryAsFile).load());

        Path blockingParent = temporaryDirectory.resolve("blocking-parent");
        Files.writeString(blockingParent, "unrelated state", StandardCharsets.UTF_8);
        Storage failingStorage = new Storage(blockingParent.resolve("tasks.txt"));
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("keep in memory"));
        StorageException saveException = assertThrows(
                StorageException.class, () ->
                failingStorage.save(tasks));

        assertEquals("Unable to load tasks", loadException.getMessage());
        assertEquals("Unable to save tasks", saveException.getMessage());
        assertEquals(1, tasks.size());
        assertEquals("keep in memory", tasks.getTask(0).getDescription());
        assertEquals("unrelated state",
                Files.readString(blockingParent, StandardCharsets.UTF_8));
    }

    /**
     * Verifies malformed entries are counted and skipped without rewriting the source file.
     */
    @Test
    public void load_malformedLines_skipsCountsAndLeavesFileUntouched() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        List<String> originalLines = List.of(
                "T | 1 | borrow book",
                "X | 0 | unknown type",
                "D | 0 | missing time",
                "T | maybe | invalid status",
                "E | 0 | truncated event | 2pm",
                "",
                "T | 0 | unexpected data | extra field",
                "T | 0 | invalid \\q escape",
                "T | 0 | trailing \\",
                "T | 0 | ",
                "D | 0 | wrong date | 02-12-2019",
                "D | 0 | impossible date | 2019-02-30",
                "D | 0 | return book | 2019-12-06");
        Files.write(dataFile, originalLines, StandardCharsets.UTF_8);

        StorageLoadResult result = new Storage(dataFile).load();

        assertEquals(11, result.getMalformedLineCount());
        assertEquals(2, result.getTaskList().size());
        assertEquals("[T][" + DisplaySymbols.getDoneMark() + "] borrow book",
                result.getTaskList().getTask(0).toString());
        assertEquals("[D][ ] return book (by: Dec 6 2019)",
                result.getTaskList().getTask(1).toString());
        assertEquals(originalLines, Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void load_corruptedRecord_blocksLaterWritesAndKeepsOriginalBytes() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String original = "T | 0 | intact\nE | 0 | broken | 5pm | 2pm\n";
        Files.writeString(dataFile, original, StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        StorageLoadResult loaded = storage.load();
        assertEquals(1, loaded.getMalformedLineCount());
        loaded.getTaskList().addTask(new Todo("later"));
        assertThrows(StorageException.class, () -> storage.save(loaded.getTaskList()));
        assertEquals(original, Files.readString(dataFile, StandardCharsets.UTF_8));
    }
}
