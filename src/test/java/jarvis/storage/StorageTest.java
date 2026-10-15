package jarvis.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jarvis.classes.Deadline;
import jarvis.classes.Event;
import jarvis.classes.Task;
import jarvis.classes.ToDo;

/** Tests persistence and reconstruction of saved tasks. */
public class StorageTest {
    /** Temporary directory used for test storage. */
    @TempDir
    Path tempDir;

    /** Storage instance used by each test. */
    private Storage storage;

    /**
     * Creates a storage instance using a temporary file before each test.
     */
    @BeforeEach
    public void setUp() {
        Path dataFile = tempDir.resolve("jarvis.txt");
        storage = new Storage(dataFile.toString());
    }


    /** Verifies that saved tasks can be loaded with their type, status, and dates intact. */
    @Test
    public void saveTasks_thenLoadTasks_restoresTasks() {
        List<Task> listTasks = new ArrayList<>();
        ToDo todoTask = new ToDo("Read book");
        Deadline deadlineTask = new Deadline("Submit report", LocalDateTime.of(2026, 8, 22, 0, 0));
        Event eventTask = new Event("Team meeting", LocalDateTime.of(2026, 8, 22, 9, 0),
                        LocalDateTime.of(2026, 8, 22, 10, 0));

        listTasks.add(todoTask);
        listTasks.add(deadlineTask);
        listTasks.add(eventTask);

        listTasks.get(2).setCompletionStatus(Task.CompletionStatus.DONE);

        Storage.saveTasks(listTasks);
        List<Task> loadedTasks = Storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][] Read book", loadedTasks.get(0).toString());
        assertEquals("[D][] Submit report (by: 08 22 2026)", loadedTasks.get(1).toString());
        assertEquals("[E][X] Team meeting (from: 08 22 2026 09:00 to: 08 22 2026 10:00)",
                loadedTasks.get(2).toString());

        loadedTasks.remove(1);
        loadedTasks.get(0).setCompletionStatus(Task.CompletionStatus.DONE);
        loadedTasks.get(1).setCompletionStatus(Task.CompletionStatus.UNDONE);

        assertEquals(2, loadedTasks.size());
        assertEquals("[T][X] Read book", loadedTasks.get(0).toString());
        assertEquals("[E][] Team meeting (from: 08 22 2026 09:00 to: 08 22 2026 10:00)",
                loadedTasks.get(1).toString());
    }

    /** */
    @Test
    public void repeatedlySaveTasks_thenLoadTasks() {
        List<Task> firstLoad = new ArrayList<>();
        ToDo todoTask = new ToDo("Read book");
        Deadline deadlineTask = new Deadline("Submit report", LocalDateTime.of(2026, 8, 22, 0, 0));
        Event eventTask = new Event("Team meeting", LocalDateTime.of(2026, 8, 22, 9, 0),
                LocalDateTime.of(2026, 8, 22, 10, 0));

        firstLoad.add(todoTask);
        firstLoad.add(deadlineTask);
        firstLoad.add(eventTask);

        // This is to test for issues with formatting when loading and printing from data file
        Storage.saveTasks(firstLoad);
        List<Task> secondLoad = Storage.loadTasks();

        assertEquals(3, secondLoad.size());
        assertEquals("[T][] Read book", secondLoad.get(0).toString());
        assertEquals("[D][] Submit report (by: 08 22 2026)", secondLoad.get(1).toString());
        assertEquals("[E][] Team meeting (from: 08 22 2026 09:00 to: 08 22 2026 10:00)",
                secondLoad.get(2).toString());

        Storage.saveTasks(secondLoad);
        List<Task> thirdLoad = Storage.loadTasks();

        assertEquals(3, thirdLoad.size());
        assertEquals("[T][] Read book", thirdLoad.get(0).toString());
        assertEquals("[D][] Submit report (by: 08 22 2026)", thirdLoad.get(1).toString());
        assertEquals("[E][] Team meeting (from: 08 22 2026 09:00 to: 08 22 2026 10:00)",
                thirdLoad.get(2).toString());
    }
}
