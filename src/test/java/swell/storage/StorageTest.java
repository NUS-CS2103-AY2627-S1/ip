package swell.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import swell.exception.SwellException;
import swell.task.Deadline;
import swell.task.Event;
import swell.task.TaskDateTime;
import swell.task.TaskList;
import swell.task.Todo;

public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void loadTasks_missingDataFile_createsFileAndReturnsEmptyTaskList() throws SwellException {
        Path dataFile = getDataFile();
        Storage storage = new Storage(dataFile);

        TaskList tasks = storage.loadTasks();

        assertTrue(Files.exists(dataFile));
        assertEquals(0, tasks.size());
    }

    @Test
    public void saveTasks_thenLoadTasks_returnsSameTaskDetails() throws SwellException {
        Storage storage = new Storage(getDataFile());
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book", new ArrayList<>(List.of("school")));
        Deadline deadline = new Deadline("submit report",
                TaskDateTime.of(LocalDateTime.of(2026, 9, 18, 23, 59)),
                new ArrayList<>(List.of("cs2103")));
        Event event = new Event("project meeting",
                TaskDateTime.of(LocalDateTime.of(2026, 9, 19, 14, 0)),
                TaskDateTime.of(LocalDateTime.of(2026, 9, 19, 16, 0)),
                new ArrayList<>(List.of("team")));
        deadline.markAsDone();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.saveTasks(tasks);
        TaskList loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read book #school", loadedTasks.get(0).toString());
        assertEquals("[D][X] submit report #cs2103 (by: Sep 18 2026 11:59 PM)",
                loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting #team (from: Sep 19 2026 2:00 PM "
                        + "to: Sep 19 2026 4:00 PM)",
                loadedTasks.get(2).toString());
        assertTrue(loadedTasks.get(1).isDone());
    }

    @Test
    public void loadTasks_blankLines_skipsBlankLines() throws Exception {
        Path dataFile = getDataFile();
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, System.lineSeparator()
                + "T | 0 | read+book | school" + System.lineSeparator()
                + System.lineSeparator());
        Storage storage = new Storage(dataFile);

        TaskList tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book #school", tasks.get(0).toString());
        assertIterableEquals(List.of("school"), tasks.get(0).getTags());
    }

    @Test
    public void loadTasks_unknownTaskType_throwsSwellException() throws Exception {
        Path dataFile = getDataFile();
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "X | 0 | mystery");
        Storage storage = new Storage(dataFile);

        assertThrows(SwellException.class, storage::loadTasks);
    }

    @Test
    public void loadTasks_invalidDoneStatus_throwsSwellException() throws Exception {
        Path dataFile = getDataFile();
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "T | 2 | read+book");
        Storage storage = new Storage(dataFile);

        assertThrows(SwellException.class, storage::loadTasks);
    }

    @Test
    public void loadTasks_invalidSavedDeadlineDate_throwsSwellException() throws Exception {
        Path dataFile = getDataFile();
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "D | 0 | return+book | Sunday");
        Storage storage = new Storage(dataFile);

        assertThrows(SwellException.class, storage::loadTasks);
    }

    private Path getDataFile() {
        return tempDir.resolve("data").resolve("swell.txt");
    }
}
