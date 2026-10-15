package amigobot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import amigobot.task.Deadline;
import amigobot.task.Event;
import amigobot.task.Task;
import amigobot.task.TaskList;
import amigobot.task.Todo;

/**
 * Tests for Storage's save/load round-trip, which also exercises
 * the private parseLine() and toFileFormat() methods.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void saveAndLoad_todoTask_roundTrips() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        TaskList original = new TaskList();
        original.addTask(new Todo("read book"));
        storage.save(original);

        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
    }

    @Test
    public void saveAndLoad_deadlineWithDate_roundTrips() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        TaskList original = new TaskList();
        original.addTask(new Deadline("return book", LocalDate.of(2025, 12, 2)));
        storage.save(original);

        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("[D][ ] return book (by: Dec 2 2025)", loaded.get(0).toString());
    }

    @Test
    public void saveAndLoad_deadlineWithString_roundTrips() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        TaskList original = new TaskList();
        original.addTask(new Deadline("return book", "next week"));
        storage.save(original);

        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("[D][ ] return book (by: next week)", loaded.get(0).toString());
    }

    @Test
    public void saveAndLoad_eventWithDates_roundTrips() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        TaskList original = new TaskList();
        original.addTask(new Event("conference",
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 3)));
        storage.save(original);

        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("[E][ ] conference (from: Aug 1 2025 to: Aug 3 2025)",
                loaded.get(0).toString());
    }

    @Test
    public void saveAndLoad_markedDoneTask_preservesDoneStatus() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        TaskList original = new TaskList();
        Todo todo = new Todo("done task");
        todo.markAsDone();
        original.addTask(todo);
        storage.save(original);

        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).isDone());
        assertEquals("[T][X] done task", loaded.get(0).toString());
    }

    @Test
    public void load_corruptedLine_skipsWithoutCrashing() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        // Write a file with one valid and one corrupted line
        List<String> lines = List.of(
                "T | 0 | valid task",
                "INVALID LINE",
                "T | 1 | another valid task"
        );
        Files.write(file, lines);

        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        // Only the two valid lines should be loaded
        assertEquals(2, loaded.size());
        assertEquals("valid task", loaded.get(0).getDescription());
        assertEquals("another valid task", loaded.get(1).getDescription());
    }

    @Test
    public void load_nonExistentFile_returnsEmptyList() throws IOException {
        Path file = tempDir.resolve("does-not-exist.txt");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(0, loaded.size());
    }

    @Test
    public void saveAndLoad_multipleMixedTasks_roundTrips() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        TaskList original = new TaskList();
        original.addTask(new Todo("buy groceries"));
        Deadline deadline = new Deadline("submit essay", LocalDate.of(2025, 3, 15));
        deadline.markAsDone();
        original.addTask(deadline);
        original.addTask(new Event("meeting", "Mon 2pm", "4pm"));
        storage.save(original);

        ArrayList<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("[T][ ] buy groceries", loaded.get(0).toString());
        assertEquals("[D][X] submit essay (by: Mar 15 2025)", loaded.get(1).toString());
        assertEquals("[E][ ] meeting (from: Mon 2pm to: 4pm)", loaded.get(2).toString());
    }
}
