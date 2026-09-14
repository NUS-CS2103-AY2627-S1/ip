package toot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import toot.TootException;
import toot.task.Deadline;
import toot.task.Event;
import toot.task.Task;
import toot.task.Todo;

/**
 * Tests task persistence and invalid-data handling.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyList() throws TootException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/toot.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveThenLoad_allTaskTypesAndEscapedText_roundTripsExactly() throws TootException {
        Path dataFile = temporaryDirectory.resolve("nested/data/toot.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("draft | revise\nsubmit\\copy");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 31));
        Event event = new Event("meeting", "Mon | noon", "Tue\\night");

        storage.save(List.of(todo, deadline, event));
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals(todo.getDescription(), loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals(deadline.getDescription(), loadedDeadline.getDescription());
        assertEquals(deadline.getBy(), loadedDeadline.getBy());
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals(event.getDescription(), loadedEvent.getDescription());
        assertEquals(event.getFrom(), loadedEvent.getFrom());
        assertEquals(event.getTo(), loadedEvent.getTo());
    }

    @Test
    public void load_invalidSecondLine_reportsItsLineNumber() throws IOException {
        Path dataFile = temporaryDirectory.resolve("toot.txt");
        Files.writeString(dataFile, "T | 0 | valid\nD | 0 | broken | not-a-date\n");
        Storage storage = new Storage(dataFile);

        TootException exception = assertThrows(TootException.class, storage::load);

        assertEquals("Saved task data is invalid on line 2.", exception.getMessage());
    }
}
