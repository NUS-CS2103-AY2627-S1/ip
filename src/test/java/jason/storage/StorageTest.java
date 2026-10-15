package jason.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jason.task.Deadline;
import jason.task.Event;
import jason.task.Task;
import jason.task.TaskList;
import jason.task.ToDo;

/** Tests task persistence and error handling in {@link Storage}. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_tasksRoundTripWithStatusAndDates() throws Exception {
        Path file = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(file.toString());
        TaskList original = new TaskList();
        ToDo todo = new ToDo("read book");
        todo.markComplete();
        original.add(todo);
        original.add(new ToDo("read report", Duration.ofMinutes(90)));
        original.add(new Deadline("submit report", LocalDateTime.of(2019, 10, 15, 18, 0)));
        original.add(new Event("planning", LocalDateTime.of(2020, 1, 2, 14, 0),
                LocalDateTime.of(2020, 1, 2, 16, 30)));

        storage.save(original);
        assertEquals(List.of(
                "T | 1 | read book",
                "T | 0 | read report | 90",
                "D | 0 | submit report | 2019-10-15T18:00",
                "E | 0 | planning | 2020-01-02T14:00 | 2020-01-02T16:30"), Files.readAllLines(file));
        List<Task> loaded = storage.load();

        assertEquals(4, loaded.size());
        assertTrue(loaded.get(0).isCompleted());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals(Duration.ofMinutes(90), assertInstanceOf(ToDo.class, loaded.get(1)).getDuration());
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                assertInstanceOf(Deadline.class, loaded.get(2)).getDeadline());
        Event event = assertInstanceOf(Event.class, loaded.get(3));
        assertEquals(LocalDateTime.of(2020, 1, 2, 14, 0), event.getStartDate());
        assertEquals(LocalDateTime.of(2020, 1, 2, 16, 30), event.getEndDate());
    }

    @Test
    void save_emptyTaskList_createsEmptyFile() throws Exception {
        Path file = temporaryDirectory.resolve("empty.txt");
        Storage storage = new Storage(file.toString());

        storage.save(new TaskList());

        assertTrue(Files.exists(file));
        assertEquals("", Files.readString(file));
    }

    @Test
    void load_malformedRecords_skipsInvalidLinesAndKeepsValidTasks() throws Exception {
        Path file = temporaryDirectory.resolve("malformed.txt");
        Files.write(file, List.of(
                "T | 0 | valid task",
                "T | 0 | valid duration task | 90",
                "not a task",
                "D | 1 | invalid date | nope",
                "E | 0 | incomplete event | 2020-01-02T14:00",
                "X | 0 | unknown type",
                "T | 1 | another valid task"));
        Storage storage = new Storage(file.toString());

        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("valid task", loaded.get(0).getDescription());
        assertEquals(Duration.ofMinutes(90), assertInstanceOf(ToDo.class, loaded.get(1)).getDuration());
        assertEquals("another valid task", loaded.get(2).getDescription());
        assertFalse(loaded.get(0).isCompleted());
        assertTrue(loaded.get(2).isCompleted());
    }

    @Test
    void constructor_emptyPath_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Storage(" "));
        assertThrows(IllegalArgumentException.class, () -> new Storage(null));
    }

    @Test
    void save_nullTaskList_throwsException() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());

        assertThrows(IllegalArgumentException.class, () -> storage.save(null));
    }
}
