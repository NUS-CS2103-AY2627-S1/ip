package ace.storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ace.task.Deadline;
import ace.task.Event;
import ace.task.Task;
import ace.task.TaskManager;
import ace.task.Todo;

/**
 * Storage's save path (data/Ace.txt) is hardcoded, not injectable, so these tests
 * back up and restore the developer's real save file around each test rather than
 * risk losing it.
 */
public class StorageTest {
    private static final Path SAVE_FILE = Paths.get("data", "Ace.txt");
    private byte[] backup;
    private boolean wasFileExisting;

    @BeforeEach
    public void backUpRealSaveFile() throws IOException {
        wasFileExisting = Files.exists(SAVE_FILE);
        if (wasFileExisting) {
            backup = Files.readAllBytes(SAVE_FILE);
        }
    }

    @AfterEach
    public void restoreRealSaveFile() throws IOException {
        if (wasFileExisting) {
            Files.write(SAVE_FILE, backup);
        } else {
            Files.deleteIfExists(SAVE_FILE);
        }
    }

    @Test
    public void saveThenLoad_mixOfTaskTypes_roundTripsCorrectly() throws Exception {
        TaskManager original = new TaskManager();
        original.addTask(new Todo("borrow book"));
        original.addTask(new Deadline("return book", "2/12/2019 1800"));
        original.getTask(0).completeTask();

        Storage.save(original);

        TaskManager loaded = new TaskManager();
        Storage.load(loaded);

        assertEquals(2, loaded.getCurrentNumberOfTasks());
        assertEquals(original.getTask(0).toString(), loaded.getTask(0).toString());
        assertEquals(original.getTask(1).toString(), loaded.getTask(1).toString());
    }

    @Test
    public void saveThenLoad_eventTask_roundTripsCorrectly() throws Exception {
        TaskManager original = new TaskManager();
        original.addTask(new Event("project meeting", "Mon 2pm", "4pm"));

        Storage.save(original);

        TaskManager loaded = new TaskManager();
        Storage.load(loaded);

        assertEquals(1, loaded.getCurrentNumberOfTasks());
        assertEquals(original.getTask(0).toString(), loaded.getTask(0).toString());
    }

    @Test
    public void saveThenLoad_completedEvent_preservesMarkedState() throws Exception {
        TaskManager original = new TaskManager();
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        event.completeTask();
        original.addTask(event);

        Storage.save(original);

        TaskManager loaded = new TaskManager();
        Storage.load(loaded);

        assertTrue(loaded.getTask(0).isCompleted());
    }

    @Test
    public void load_unrecognizedTypeCharacter_skipsLineWithoutCrashing() throws IOException {
        Files.createDirectories(SAVE_FILE.getParent());
        Files.writeString(SAVE_FILE,
                "[T][ ] good task\n"
                + "[Z][ ] unknown type character\n");

        TaskManager taskManager = new TaskManager();
        assertDoesNotThrow(() -> Storage.load(taskManager));

        assertEquals(1, taskManager.getCurrentNumberOfTasks());
        assertEquals("[T][ ] good task", taskManager.getTask(0).toString());
    }

    @Test
    public void load_fileDoesNotExist_leavesTaskManagerEmpty() throws IOException {
        Files.deleteIfExists(SAVE_FILE);

        TaskManager taskManager = new TaskManager();
        Storage.load(taskManager);

        assertEquals(0, taskManager.getCurrentNumberOfTasks());
    }

    @Test
    public void load_corruptedLineAmongGoodOnes_skipsBadLineWithoutCrashing() throws IOException {
        Files.createDirectories(SAVE_FILE.getParent());
        Files.writeString(SAVE_FILE,
                "[T][ ] good task\n"
                + "bad\n"
                + "[D][ ] broken deadline (by: not-a-date)\n");

        TaskManager taskManager = new TaskManager();
        assertDoesNotThrow(() -> Storage.load(taskManager));

        assertEquals(1, taskManager.getCurrentNumberOfTasks());
        Task onlyTask = taskManager.getTask(0);
        assertEquals("[T][ ] good task", onlyTask.toString());
    }
}
