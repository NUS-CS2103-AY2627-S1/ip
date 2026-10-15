package cooper.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cooper.exception.CooperException;
import cooper.task.Deadline;
import cooper.task.Event;
import cooper.task.Task;
import cooper.task.ToDo;

/** Tests decoding and file round trips for persisted tasks. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void decodeTask_validTaskTypes_returnsMatchingTasks() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());

        Task todo = storage.decodeTask("T | 0 | read book");
        Task deadline = storage.decodeTask("D | 1 | submit report | 2026-08-30T23:59");
        Task event = storage.decodeTask(
                "E | 0 | meeting | 2026-08-30T14:00 | 2026-08-30T16:00");

        assertInstanceOf(ToDo.class, todo);
        assertInstanceOf(Deadline.class, deadline);
        assertInstanceOf(Event.class, event);
        assertEquals("T | 0 | read book", todo.toDataString());
        assertEquals("D | 1 | submit report | 2026-08-30T23:59", deadline.toDataString());
        assertEquals("E | 0 | meeting | 2026-08-30T14:00 | 2026-08-30T16:00",
                event.toDataString());
    }

    @Test
    public void decodeTask_invalidEntryHeader_throwsCooperException() {
        assertThrows(CooperException.class, () -> new Storage("unused").decodeTask("T | description"));
        assertThrows(CooperException.class, () ->
                new Storage("unused").decodeTask("TODO | 0 | description"));
        assertThrows(CooperException.class, () ->
                new Storage("unused").decodeTask("T | yes | description"));
        assertThrows(CooperException.class, () ->
                new Storage("unused").decodeTask("X | 0 | description"));
    }

    @Test
    public void decodeTask_wrongFieldCount_throwsCooperException() {
        Storage storage = new Storage("unused");

        assertThrows(CooperException.class, () -> storage.decodeTask("T | 0 | todo | extra"));
        assertThrows(CooperException.class, () -> storage.decodeTask("D | 0 | deadline"));
        assertThrows(CooperException.class, () ->
                storage.decodeTask("E | 0 | event | 2026-08-30T14:00"));
    }

    @Test
    public void saveAndLoadTasks_multipleTaskTypes_preservesTaskData() {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        List<Task> originalTasks = List.of(
                new ToDo("read book", true),
                new Deadline("submit report", LocalDateTime.of(2026, 8, 30, 23, 59)),
                new Event("meeting", true,
                        LocalDateTime.of(2026, 8, 30, 14, 0),
                        LocalDateTime.of(2026, 8, 30, 16, 0)));

        storage.saveTasks(originalTasks);
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(originalTasks.stream().map(Task::toDataString).toList(),
                loadedTasks.stream().map(Task::toDataString).toList());
    }

    @Test
    public void loadTasks_fileDoesNotExist_createsFileAndReturnsEmptyList() {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());

        assertEquals(List.of(), storage.loadTasks());
        assertTrue(dataFile.toFile().isFile());
    }
}
