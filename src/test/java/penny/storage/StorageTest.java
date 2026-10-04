package penny.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import penny.common.PennyException;
import penny.task.Deadline;
import penny.task.Event;
import penny.task.Task;
import penny.task.TaskList;
import penny.task.Todo;

/**
 * Unit test suite for {@link Storage}.
 * Verifies loading, saving, directory auto-creation, and corrupted data error handling.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    @Test
    public void load_nonExistentFile_returnsEmptyList() throws PennyException {
        Path filePath = tempDir.resolve("non_existent_file.txt");
        Storage storage = new Storage(filePath);
        List<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_emptyFile_returnsEmptyList() throws IOException, PennyException {
        Path filePath = tempDir.resolve("empty.txt");
        Files.createFile(filePath);

        Storage storage = new Storage(filePath);
        List<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_validTodoDeadlineEvent_returnsPopulatedList() throws IOException, PennyException {
        Path filePath = tempDir.resolve("tasks.txt");
        List<String> lines = List.of(
                "T | 1 | read book",
                "D | 0 | submit assignment | 2019-10-15 1800",
                "E | 0 | project meeting | 2019-10-15 1400 | 2019-10-15 1600"
        );
        Files.write(filePath, lines);

        Storage storage = new Storage(filePath);
        List<Task> tasks = storage.load();

        assertEquals(3, tasks.size());
        assertTrue(tasks.get(0) instanceof Todo);
        assertTrue(tasks.get(0).isDone());
        assertEquals("read book", tasks.get(0).getDescription());

        assertTrue(tasks.get(1) instanceof Deadline);
        assertFalse(tasks.get(1).isDone());

        assertTrue(tasks.get(2) instanceof Event);
        assertFalse(tasks.get(2).isDone());
    }

    @Test
    public void load_corruptedInsufficientTokens_throwsPennyException() throws IOException {
        Path filePath = tempDir.resolve("corrupted.txt");
        Files.write(filePath, List.of("T | 1"));

        Storage storage = new Storage(filePath);
        PennyException exception = assertThrows(PennyException.class, () -> {
            storage.load();
        });
        assertTrue(exception.getMessage().contains("insufficient fields"));
    }

    @Test
    public void load_corruptedInvalidStatusFlag_throwsPennyException() throws IOException {
        Path filePath = tempDir.resolve("corrupted_status.txt");
        Files.write(filePath, List.of("T | X | read book"));

        Storage storage = new Storage(filePath);
        PennyException exception = assertThrows(PennyException.class, () -> {
            storage.load();
        });
        assertTrue(exception.getMessage().contains("Invalid status indicator"));
    }

    @Test
    public void load_corruptedMissingDescription_throwsPennyException() throws IOException {
        Path filePath = tempDir.resolve("corrupted_desc.txt");
        Files.write(filePath, List.of("D | 0 |   | 2019-10-15"));

        Storage storage = new Storage(filePath);
        PennyException exception = assertThrows(PennyException.class, () -> {
            storage.load();
        });
        assertTrue(exception.getMessage().contains("Missing task description"));
    }

    @Test
    public void load_corruptedMissingDeadlineBy_throwsPennyException() throws IOException {
        Path filePath = tempDir.resolve("corrupted_deadline.txt");
        Files.write(filePath, List.of("D | 0 | return book"));

        Storage storage = new Storage(filePath);
        PennyException exception = assertThrows(PennyException.class, () -> {
            storage.load();
        });
        assertTrue(exception.getMessage().contains("missing deadline time"));
    }

    @Test
    public void load_corruptedMissingEventTimes_throwsPennyException() throws IOException {
        Path filePath = tempDir.resolve("corrupted_event.txt");
        Files.write(filePath, List.of("E | 0 | party | 2019-10-15"));

        Storage storage = new Storage(filePath);
        PennyException exception = assertThrows(PennyException.class, () -> {
            storage.load();
        });
        assertTrue(exception.getMessage().contains("missing start or end time"));
    }

    @Test
    public void load_corruptedUnknownTaskType_throwsPennyException() throws IOException {
        Path filePath = tempDir.resolve("unknown_type.txt");
        Files.write(filePath, List.of("Z | 0 | something unknown"));

        Storage storage = new Storage(filePath);
        PennyException exception = assertThrows(PennyException.class, () -> {
            storage.load();
        });
        assertTrue(exception.getMessage().contains("Unknown task type"));
    }

    @Test
    public void save_missingParentDirectory_createsDirectoriesAutomatically() throws PennyException {
        Path nestedPath = tempDir.resolve("nested").resolve("sub").resolve("tasks.txt");
        Storage storage = new Storage(nestedPath);

        TaskList taskList = new TaskList();
        taskList.add(new Todo("nested task"));
        storage.save(taskList);

        assertTrue(Files.exists(nestedPath));
    }

    @Test
    public void saveAndLoad_roundTrip_preservesAllTaskDataAndDoneStates()
            throws PennyException, IOException {
        Path filePath = tempDir.resolve("round_trip.txt");
        Storage storage = new Storage(filePath);

        TaskList originalList = new TaskList();
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", "2024-11-20 2359");
        Event event = new Event("annual conference", "2024-11-25 0900", "2024-11-27 1700");

        originalList.add(todo);
        originalList.add(deadline);
        originalList.add(event);

        storage.save(originalList);
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("borrow book", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(0).isDone());

        assertEquals("submit report", loadedTasks.get(1).getDescription());
        assertFalse(loadedTasks.get(1).isDone());

        assertEquals("annual conference", loadedTasks.get(2).getDescription());
        assertFalse(loadedTasks.get(2).isDone());
    }
}
