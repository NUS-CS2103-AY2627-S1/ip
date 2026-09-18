package rene.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import rene.exception.ReneException;
import rene.task.Deadline;
import rene.task.Event;
import rene.task.Task;
import rene.task.Todo;

class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void loadTasks_missingFile_createsEmptyFile() throws ReneException {
        Path dataFile = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(dataFile);

        List<Task> tasks = storage.loadTasks().tasks();

        assertTrue(tasks.isEmpty());
        assertTrue(Files.exists(dataFile));
    }

    @Test
    void saveTasks_thenLoadTasks_preservesTaskDetailsAndStatus() throws ReneException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 31));
        Event event = new Event("lecture", "2pm", "4pm");
        deadline.markAsDone();
        event.markAsDone();

        storage.saveTasks(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.loadTasks().tasks();

        assertEquals(3, loadedTasks.size());
        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read book", loadedTodo.getDescription());
        assertFalse(loadedTodo.isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 31), loadedDeadline.getDueDate());
        assertTrue(loadedDeadline.isDone());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("lecture", loadedEvent.getDescription());
        assertEquals("2pm", loadedEvent.getFrom());
        assertEquals("4pm", loadedEvent.getTo());
        assertTrue(loadedEvent.isDone());
    }

    @Test
    void loadTasks_malformedSecondLine_keepsValidTasksAndReportsItsLineNumber() throws IOException, ReneException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(
                dataFile,
                "T | 0 | first valid task\nD | 0 | invalid deadline | tomorrow\nT | 0 | last valid task\n",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.loadTasks();

        assertEquals(List.of("first valid task", "last valid task"),
                result.tasks().stream().map(Task::getDescription).toList());
        assertEquals(List.of("I couldn't understand line 2 in " + dataFile + "."), result.warnings());
    }
}
