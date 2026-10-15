package nob.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nob.exception.NobException;
import nob.task.Deadline;
import nob.task.Event;
import nob.task.Task;
import nob.task.Todo;

/**
 * Tests persistence of Nob tasks through {@link Storage}.
 */
public class StorageTest {
    @Test
    public void constructor_nullPath_assertionError() {
        assertThrows(AssertionError.class, () -> new Storage(null));
    }

    /**
     * Verifies that storage requires a path with a parent directory.
     */
    @Test
    public void constructor_pathWithoutParent_assertionError() {
        assertThrows(AssertionError.class, () -> new Storage(Path.of("nob.txt")));
    }

    /**
     * Verifies that saved task types and completion statuses are restored accurately.
     */
    @Test
    public void saveTasks_thenLoadTasks_tasksRestored(@TempDir Path temporaryDirectory) throws NobException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("nob.txt");
        Storage storage = new Storage(dataFile);
        Task completedTodo = new Todo("read book");
        completedTodo.markAsDone();
        Task[] savedTasks = {
            completedTodo,
            new Deadline("return book", "Friday"),
            new Event("team sync", "Mon 2pm", "4pm")
        };

        assertTrue(storage.saveTasks(savedTasks, savedTasks.length));

        Task[] loadedTasks = new Task[3];
        assertEquals(3, storage.loadTasks(loadedTasks));
        assertEquals("[T] read book [✓]", loadedTasks[0].toString());
        assertEquals("[D: Friday] return book [ ]", loadedTasks[1].toString());
        assertEquals("[E: Mon 2pm to 4pm] team sync [ ]", loadedTasks[2].toString());
    }

    /**
     * Verifies that loading from a file that does not exist returns no tasks.
     */
    @Test
    public void loadTasks_missingFile_noTasksLoaded(@TempDir Path temporaryDirectory) throws NobException {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("nob.txt"));

        assertEquals(0, storage.loadTasks(new Task[2]));
    }

    @Test
    public void loadTasks_nullDestination_assertionError(@TempDir Path temporaryDirectory) {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("nob.txt"));

        assertThrows(AssertionError.class, () -> storage.loadTasks(null));
    }

    @Test
    public void loadTasks_moreSavedTasksThanCapacity_onlyCapacityLoaded(@TempDir Path temporaryDirectory)
            throws IOException, NobException {
        Path dataFile = temporaryDirectory.resolve("nob.txt");
        Files.writeString(dataFile, "[T] first [ ]\n[T] second [ ]\n");
        Storage storage = new Storage(dataFile);
        Task[] loadedTasks = new Task[1];

        assertEquals(1, storage.loadTasks(loadedTasks));
        assertEquals("[T] first [ ]", loadedTasks[0].toString());
    }

    @Test
    public void loadTasks_unreadablePath_exceptionThrown(@TempDir Path temporaryDirectory) throws IOException {
        Path directoryPath = temporaryDirectory.resolve("nob.txt");
        Files.createDirectory(directoryPath);
        Storage storage = new Storage(directoryPath);

        NobException exception = assertThrows(NobException.class, () -> storage.loadTasks(new Task[1]));

        assertEquals("I couldn't load the saved tasks, so I'm starting with an empty list.",
                exception.getMessage());
    }

    /**
     * Verifies that malformed saved records are skipped while valid records are loaded.
     */
    @Test
    public void loadTasks_malformedRecords_validRecordsLoaded(@TempDir Path temporaryDirectory)
            throws IOException, NobException {
        Path dataFile = temporaryDirectory.resolve("nob.txt");
        Files.writeString(dataFile, "not a task\n"
                + "[T] [ ]\n"
                + "[T] wrong status [?]\n"
                + "[D: ] missing deadline [ ]\n"
                + "[D: Friday] [ ]\n"
                + "[E: Monday] missing separator [ ]\n"
                + "[E:  to Tuesday] missing start [ ]\n"
                + "[E: Monday to ] missing end [ ]\n"
                + "[E: Monday to Tuesday] [ ]\n"
                + "[T] read book [ ]\n");
        Storage storage = new Storage(dataFile);
        Task[] loadedTasks = new Task[3];

        assertEquals(1, storage.loadTasks(loadedTasks));
        assertEquals("[T] read book [ ]", loadedTasks[0].toString());
        assertNull(loadedTasks[1]);
    }

    @Test
    public void loadTasks_validCompletedDeadlineAndEvent_completionRestored(@TempDir Path temporaryDirectory)
            throws IOException, NobException {
        Path dataFile = temporaryDirectory.resolve("nob.txt");
        Files.writeString(dataFile, "[D: Friday] return book [✓]\n"
                + "[E: Monday to Tuesday] team retreat [✓]\n");
        Storage storage = new Storage(dataFile);
        Task[] loadedTasks = new Task[2];

        assertEquals(2, storage.loadTasks(loadedTasks));
        assertEquals("[D: Friday] return book [✓]", loadedTasks[0].toString());
        assertEquals("[E: Monday to Tuesday] team retreat [✓]", loadedTasks[1].toString());
    }

    @Test
    public void saveTasks_emptyList_emptyFileCreated(@TempDir Path temporaryDirectory) throws IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("nob.txt");
        Storage storage = new Storage(dataFile);

        assertTrue(storage.saveTasks(new Task[0], 0));
        assertTrue(Files.exists(dataFile));
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    public void saveTasks_unwritableParent_falseReturned(@TempDir Path temporaryDirectory) throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "content");
        Storage storage = new Storage(parentFile.resolve("nob.txt"));

        assertFalse(storage.saveTasks(new Task[0], 0));
    }

    @Test
    public void saveTasks_invalidArguments_assertionError(@TempDir Path temporaryDirectory) {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("nob.txt"));

        assertThrows(AssertionError.class, () -> storage.saveTasks(null, 0));
        assertThrows(AssertionError.class, () -> storage.saveTasks(new Task[1], -1));
        assertThrows(AssertionError.class, () -> storage.saveTasks(new Task[1], 2));
        assertThrows(AssertionError.class, () -> storage.saveTasks(new Task[1], 1));
    }
}
