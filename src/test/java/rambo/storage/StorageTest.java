package rambo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rambo.exception.RamboException;
import rambo.task.DeadlineTask;
import rambo.task.EventTask;
import rambo.task.Task;

/**
 * Tests persistence and rejection of invalid task records.
 */
public class StorageTest {
    private static final Path DATA_FILE = Path.of("./data/Rambo.txt");

    @BeforeEach
    void setUp() throws IOException {
        deleteTestData();
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteTestData();
    }

    @Test
    void loadTasks_missingDataFile_returnsEmptyList() {
        Storage storage = new Storage();

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    void saveAndLoadTasks_differentTaskTypes_preservesTaskData() {
        Storage storage = new Storage();
        Task todo = new Task("buy milk");
        DeadlineTask deadline = new DeadlineTask("submit report", "2026-09-15");
        EventTask event = new EventTask("project meeting", "2026-09-20", "2026-09-22");
        deadline.toggleDone();
        todo.setPriorityLevel(1);
        event.setPriorityLevel(3);

        storage.saveTasks(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("T||buy milk|1", loadedTasks.get(0).toDataString());
        assertInstanceOf(DeadlineTask.class, loadedTasks.get(1));
        assertEquals("D|X|submit report|2026-09-15|0", loadedTasks.get(1).toDataString());
        assertInstanceOf(EventTask.class, loadedTasks.get(2));
        assertEquals("E||project meeting|2026-09-20|2026-09-22|3", loadedTasks.get(2).toDataString());
    }

    @Test
    void saveTasks_missingDataDirectory_createsDirectoryAndFile() {
        Storage storage = new Storage();

        storage.saveTasks(List.of(new Task("buy milk")));

        assertTrue(Files.exists(DATA_FILE));
    }

    @Test
    void saveTasks_existingData_replacesFileContents() throws IOException {
        Storage storage = new Storage();
        storage.saveTasks(List.of(new Task("old task")));

        storage.saveTasks(List.of(new Task("new task")));

        assertEquals(List.of("T||new task|0"), Files.readAllLines(DATA_FILE));
    }

    @Test
    void loadTasks_unknownTaskType_throwsRamboException() throws IOException {
        writeTestData("Z||unknown task");
        Storage storage = new Storage();

        assertThrows(RamboException.class, storage::loadTasks);
    }

    @Test
    void loadTasks_separatorOnlyRecords_throwsRamboException() throws IOException {
        Storage storage = new Storage();

        for (String record : new String[] {"|", "|||"}) {
            writeTestData(record);

            RamboException exception = assertThrows(RamboException.class, storage::loadTasks);
            assertEquals("Rambo: Invalid task record: " + record, exception.getMessage());
        }
    }

    @Test
    void loadTasks_invalidDoneMarker_throwsRamboException() throws IOException {
        writeTestData("T|DONE|buy milk");
        Storage storage = new Storage();

        assertThrows(RamboException.class, storage::loadTasks);
    }

    @Test
    void loadTasks_legacyRecords_loadsTasksWithoutPriority() throws IOException {
        writeTestData("T||buy milk\n"
                + "D|X|submit report|2026-09-15\n"
                + "E||project meeting|2026-09-20|2026-09-22");
        Storage storage = new Storage();

        List<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertTrue(tasks.stream().noneMatch(Task::hasPriority));
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    void loadTasks_invalidPriority_throwsRamboException() throws IOException {
        writeTestData("T||buy milk|4");
        Storage storage = new Storage();

        assertThrows(RamboException.class, storage::loadTasks);
    }

    private void writeTestData(String content) throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        Files.writeString(DATA_FILE, content);
    }

    private void deleteTestData() throws IOException {
        Files.deleteIfExists(DATA_FILE);
        Files.deleteIfExists(DATA_FILE.getParent());
    }
}
