package matthew.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import matthew.exception.MatthewException;
import matthew.task.Deadline;
import matthew.task.Event;
import matthew.task.TaskList;
import matthew.task.Todo;

/** Tests task persistence, including malformed saved data. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadCreatesMissingFileAndSaveRoundTripsEveryTaskType() throws Exception {
        Path path = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(path.toString());
        assertTrue(storage.load().isEmpty());
        assertTrue(Files.exists(path));

        Todo todo = new Todo("buy milk");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit", LocalDateTime.of(2026, 8, 22, 12, 0));
        Event event = new Event("camp", LocalDateTime.of(2026, 8, 22, 9, 0),
                LocalDateTime.of(2026, 8, 23, 17, 0));
        TaskList tasks = new TaskList();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.save(tasks);
        TaskList reloaded = new TaskList(storage.load());
        assertEquals(3, reloaded.size());
        assertEquals("[T][X] buy milk", reloaded.get(1).toString());
        assertEquals(deadline.toString(), reloaded.get(2).toString());
        assertEquals(event.toString(), reloaded.get(3).toString());
    }

    @Test
    void loadRejectsCorruptedData() throws Exception {
        Path path = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(path, "D | 1 | submit | not-a-date");

        MatthewException exception = assertThrows(MatthewException.class, () -> new Storage(path.toString()).load());
        assertEquals("The saved date/time data is corrupted.", exception.getMessage());
    }
}
