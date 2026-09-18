package zhangwei.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zhangwei.ZhangWeiException;
import zhangwei.task.Deadline;
import zhangwei.task.Event;
import zhangwei.task.Task;
import zhangwei.task.TaskList;
import zhangwei.task.Todo;

/**
 * Tests {@link Storage}, which is the only class that reads and writes the
 * user's save file. Its two risks are losing tasks and writing lines it cannot
 * read back, so the tests cover the exact text written, every way a line can be
 * damaged, and a save-then-load round trip.
 *
 * <p>Every test writes inside a temporary folder that JUnit deletes afterwards,
 * so the real {@code data/} folder is never touched.
 */
public class StorageTest {

    // JUnit injects a fresh temporary folder here; it must not be private.
    @TempDir
    Path tempDir;

    private Path saveFile;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        saveFile = tempDir.resolve("tasks.txt");
        storage = new Storage(saveFile.toString());
    }

    /** Writes the given lines into the save file. */
    private void writeSaveFile(String... lines) throws IOException {
        Files.write(saveFile, List.of(lines));
    }

    /** Loads the save file, failing the test if it cannot be read at all. */
    private Storage.LoadResult load() throws ZhangWeiException {
        return storage.loadTasks();
    }

    // ---------- loading ----------

    @Test
    public void loadTasks_fileDoesNotExist_emptyResultReturned() throws ZhangWeiException {
        // A missing file just means nothing has been saved yet.
        Storage.LoadResult result = load();
        assertTrue(result.tasks().isEmpty());
        assertEquals(0, result.skippedLines());
        assertNull(result.backupPath());
    }

    @Test
    public void loadTasks_emptyFile_noTasksReturned() throws Exception {
        writeSaveFile();
        assertTrue(load().tasks().isEmpty());
    }

    @Test
    public void loadTasks_oneTodo_todoReturned() throws Exception {
        writeSaveFile("T | 0 | read book");
        List<Task> tasks = load().tasks();
        assertEquals(1, tasks.size());
        assertInstanceOf(Todo.class, tasks.get(0));
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void loadTasks_doneTask_taskMarkedDone() throws Exception {
        writeSaveFile("T | 1 | read book");
        assertTrue(load().tasks().get(0).isDone());
    }

    @Test
    public void loadTasks_notDoneTask_taskNotMarkedDone() throws Exception {
        writeSaveFile("T | 0 | read book");
        assertFalse(load().tasks().get(0).isDone());
    }

    @Test
    public void loadTasks_deadlineLine_deadlineWithDateReturned() throws Exception {
        writeSaveFile("D | 0 | return book | 2019-06-06");
        Task task = load().tasks().get(0);
        assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDate.of(2019, 6, 6), ((Deadline) task).getBy());
    }

    @Test
    public void loadTasks_eventLine_eventWithBothDatesReturned() throws Exception {
        writeSaveFile("E | 1 | project meeting | 2019-12-03 | 2019-12-04");
        Task task = load().tasks().get(0);
        assertInstanceOf(Event.class, task);
        assertEquals(LocalDate.of(2019, 12, 3), ((Event) task).getFrom());
        assertEquals(LocalDate.of(2019, 12, 4), ((Event) task).getTo());
        assertTrue(task.isDone());
    }

    @Test
    public void loadTasks_allTaskTypes_tasksReturnedInFileOrder() throws Exception {
        writeSaveFile("T | 0 | read book",
                "D | 0 | return book | 2019-06-06",
                "E | 0 | project meeting | 2019-12-03 | 2019-12-04");
        List<Task> tasks = load().tasks();
        assertEquals(3, tasks.size());
        assertInstanceOf(Todo.class, tasks.get(0));
        assertInstanceOf(Deadline.class, tasks.get(1));
        assertInstanceOf(Event.class, tasks.get(2));
    }

    @Test
    public void loadTasks_blankLines_ignoredWithoutBeingCountedAsDamage() throws Exception {
        writeSaveFile("T | 0 | read book", "", "   ", "T | 0 | write essay");
        Storage.LoadResult result = load();
        assertEquals(2, result.tasks().size());
        assertEquals(0, result.skippedLines());
        assertNull(result.backupPath());
    }

    @Test
    public void loadTasks_fileNotValidText_exceptionThrownAndBackupKept() throws Exception {
        // 0xE9 on its own is not valid UTF-8, so the whole file is unreadable.
        byte[] original = {'T', ' ', '|', ' ', '0', ' ', '|', ' ', 'c', 'a', 'f', (byte) 0xE9};
        Files.write(saveFile, original);

        ZhangWeiException e = assertThrows(ZhangWeiException.class, this::load);

        // The next save would overwrite the file, so a copy must survive.
        Path backup = saveFile.resolveSibling("tasks.txt.corrupt");
        assertTrue(e.getMessage().contains(backup.toString()));
        assertArrayEquals(original, Files.readAllBytes(backup));
    }

    @Test
    public void loadTasks_unreadableLine_otherTasksStillLoaded() throws Exception {
        // One damaged line must not cost the user every other task.
        writeSaveFile("T | 0 | read book", "garbage", "T | 0 | write essay");
        Storage.LoadResult result = load();
        assertEquals(2, result.tasks().size());
        assertEquals(1, result.skippedLines());
    }

    @Test
    public void loadTasks_unreadableLine_backupOfOriginalFileKept() throws Exception {
        writeSaveFile("T | 0 | read book", "garbage");
        Storage.LoadResult result = load();
        assertNotNull(result.backupPath());
        assertTrue(Files.exists(result.backupPath()));
        // The backup must still contain the damaged line, so it can be repaired.
        assertEquals(List.of("T | 0 | read book", "garbage"),
                Files.readAllLines(result.backupPath()));
    }

    @Test
    public void loadTasks_unknownTaskType_lineSkipped() throws Exception {
        writeSaveFile("X | 0 | mystery");
        assertEquals(1, load().skippedLines());
    }

    @Test
    public void loadTasks_statusNeitherOneNorZero_lineSkipped() throws Exception {
        // Defaulting to "not done" would silently throw away the user's progress.
        writeSaveFile("T | 2 | read book");
        assertEquals(1, load().skippedLines());
    }

    @Test
    public void loadTasks_blankDescription_lineSkipped() throws Exception {
        writeSaveFile("T | 0 | ");
        assertEquals(1, load().skippedLines());
    }

    @Test
    public void loadTasks_tooFewFields_lineSkipped() throws Exception {
        writeSaveFile("T | 0");
        assertEquals(1, load().skippedLines());
    }

    @Test
    public void loadTasks_todoWithExtraField_lineSkipped() throws Exception {
        writeSaveFile("T | 0 | read book | 2019-06-06");
        assertEquals(1, load().skippedLines());
    }

    @Test
    public void loadTasks_deadlineWithoutDate_lineSkipped() throws Exception {
        writeSaveFile("D | 0 | return book");
        assertEquals(1, load().skippedLines());
    }

    @Test
    public void loadTasks_deadlineWithMalformedDate_lineSkipped() throws Exception {
        writeSaveFile("D | 0 | return book | 06-06-2019");
        assertEquals(1, load().skippedLines());
    }

    @Test
    public void loadTasks_eventMissingEndDate_lineSkipped() throws Exception {
        writeSaveFile("E | 0 | project meeting | 2019-12-03");
        assertEquals(1, load().skippedLines());
    }

    @Test
    public void loadTasks_severalDamagedLines_allCounted() throws Exception {
        writeSaveFile("garbage", "T | 9 | read book", "T | 0 | write essay");
        Storage.LoadResult result = load();
        assertEquals(1, result.tasks().size());
        assertEquals(2, result.skippedLines());
    }

    // ---------- saving ----------

    @Test
    public void saveTasks_everyTaskType_expectedLinesWritten() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 6, 6)));
        tasks.add(new Event("project meeting",
                LocalDate.of(2019, 12, 3), LocalDate.of(2019, 12, 4)));
        tasks.get(1).markAsDone();

        storage.saveTasks(tasks);

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-06-06",
                "E | 0 | project meeting | 2019-12-03 | 2019-12-04"),
                Files.readAllLines(saveFile));
    }

    @Test
    public void saveTasks_emptyList_emptyFileWritten() throws Exception {
        storage.saveTasks(new TaskList());
        assertTrue(Files.exists(saveFile));
        assertTrue(Files.readAllLines(saveFile).isEmpty());
    }

    @Test
    public void saveTasks_missingParentFolder_folderCreated() throws Exception {
        Path nested = tempDir.resolve("data").resolve("nested").resolve("tasks.txt");
        Storage nestedStorage = new Storage(nested.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        nestedStorage.saveTasks(tasks);

        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(nested));
    }

    @Test
    public void saveTasks_bareFileNameWithNoFolder_noExceptionThrown() throws Exception {
        // Path.getParent() is null here; saving must not trip over that.
        Path bare = tempDir.resolve("bare.txt");
        Storage bareStorage = new Storage(bare.getFileName().toString());
        try {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("read book"));
            bareStorage.saveTasks(tasks);
            assertTrue(Files.exists(Path.of("bare.txt")));
        } finally {
            Files.deleteIfExists(Path.of("bare.txt"));
        }
    }

    @Test
    public void saveTasks_existingFile_previousContentReplaced() throws Exception {
        writeSaveFile("T | 0 | old task", "T | 0 | another old task");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("new task"));

        storage.saveTasks(tasks);

        assertEquals(List.of("T | 0 | new task"), Files.readAllLines(saveFile));
    }

    @Test
    public void saveTasks_pathIsADirectory_exceptionThrown() {
        // Writing over a folder cannot work, and the user must be told.
        Storage badStorage = new Storage(tempDir.toString());
        assertThrows(ZhangWeiException.class, () -> badStorage.saveTasks(new TaskList()));
    }

    // ---------- round trip ----------

    @Test
    public void saveThenLoad_everyTaskType_tasksUnchanged() throws Exception {
        TaskList original = new TaskList();
        original.add(new Todo("read book"));
        original.add(new Deadline("return book", LocalDate.of(2019, 6, 6)));
        original.add(new Event("project meeting",
                LocalDate.of(2019, 12, 3), LocalDate.of(2019, 12, 4)));
        original.get(2).markAsDone();

        storage.saveTasks(original);
        Storage.LoadResult result = load();

        assertEquals(0, result.skippedLines());
        assertEquals(3, result.tasks().size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i + 1).toString(), result.tasks().get(i).toString());
        }
    }
}
