package della.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import della.task.Deadline;
import della.task.Event;
import della.task.Task;
import della.task.Todo;

public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void hasData_fileDoesNotExist_falseReturned() {
        Storage storage = storageFor("missing.txt");

        assertFalse(storage.hasData());
    }

    @Test
    public void hasData_fileIsEmpty_falseReturned() throws IOException {
        Files.createFile(temporaryDirectory.resolve("empty.txt"));
        Storage storage = storageFor("empty.txt");

        assertFalse(storage.hasData());
    }

    @Test
    public void storeTask_validTask_taskAppendedToFile() throws IOException {
        Storage storage = storageFor("tasks.txt");

        storage.storeTask(new Todo("read book"));

        assertTrue(storage.hasData());
        assertEquals(List.of("T|0|read book"), Files.readAllLines(temporaryDirectory.resolve("tasks.txt")));
    }

    @Test
    public void loadTasks_tasksOfEachType_tasksReconstructed() throws IOException {
        Storage storage = storageFor("tasks.txt");
        storage.storeTask(new Todo("read book", true));
        storage.storeTask(new Deadline("submit report", LocalDateTime.of(2026, 8, 26, 14, 30)));
        storage.storeTask(new Event("team meeting", true, LocalDateTime.of(2026, 8, 27, 9, 0),
                LocalDateTime.of(2026, 8, 27, 10, 0)));

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertEquals("T|1|read book", tasks.get(0).formatForStorage());
        assertEquals("D|0|submit report|Aug 26 2026 2:30pm", tasks.get(1).formatForStorage());
        assertEquals("E|1|team meeting|Aug 27 2026 9:00am|Aug 27 2026 10:00am",
                tasks.get(2).formatForStorage());
    }

    @Test
    public void loadTasks_fileDoesNotExist_exceptionThrown() {
        Storage storage = storageFor("missing.txt");

        assertThrows(FileNotFoundException.class, storage::loadTasks);
    }

    @Test
    public void updateTaskStatus_secondTask_secondLineReplaced() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(taskFile, List.of("T|0|first task", "T|0|second task"));
        Storage storage = storageFor("tasks.txt");

        storage.updateTaskStatus(2, new Todo("second task", true));

        assertEquals(List.of("T|0|first task", "T|1|second task"), Files.readAllLines(taskFile));
    }

    @Test
    public void deleteTask_secondTask_secondLineRemoved() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(taskFile, List.of("T|0|first task", "T|0|second task", "T|0|third task"));
        Storage storage = storageFor("tasks.txt");

        storage.deleteTask(2);

        assertEquals(List.of("T|0|first task", "T|0|third task"), Files.readAllLines(taskFile));
    }

    /**
     * Returns storage that uses an isolated temporary file for a test.
     */
    private Storage storageFor(String fileName) {
        return new Storage(temporaryDirectory.resolve(fileName).toString());
    }
}
