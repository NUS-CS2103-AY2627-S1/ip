package beemo.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import beemo.BeemoException;
import beemo.task.Deadline;
import beemo.task.Event;
import beemo.task.Task;
import beemo.task.Todo;

class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void loadTasks_missingFile_emptyListReturned() throws BeemoException {
        Storage storage = new Storage(tempDirectory.resolve("missing/tasks.txt"));

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    void saveAndLoadTasks_allTaskTypes_dataPreserved() throws BeemoException {
        Path filePath = tempDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(filePath);
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 30));
        Event event = new Event("meeting", "Monday", "Tuesday");

        storage.saveTasks(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read book", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 30), loadedDeadline.getDueDate());
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("meeting", loadedEvent.getDescription());
        assertEquals("Monday", loadedEvent.getStartTime());
        assertEquals("Tuesday", loadedEvent.getEndTime());
    }

    @Test
    void saveTasks_nestedPath_parentDirectoryCreated() throws BeemoException {
        Path filePath = tempDirectory.resolve("one/two/tasks.txt");
        Storage storage = new Storage(filePath);

        storage.saveTasks(List.of(new Todo("read book")));

        assertTrue(Files.exists(filePath));
    }

    @Test
    void saveAndLoadTasks_separatorInFields_dataPreserved() throws BeemoException {
        Path filePath = tempDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath);
        Todo todo = new Todo("buy milk | eggs \\ bread");
        Event event = new Event("A | B", "room \\ one", "room | two");

        storage.saveTasks(List.of(todo, event));
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals("buy milk | eggs \\ bread", loadedTasks.get(0).getDescription());
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(1));
        assertEquals("A | B", loadedEvent.getDescription());
        assertEquals("room \\ one", loadedEvent.getStartTime());
        assertEquals("room | two", loadedEvent.getEndTime());
    }

    @Test
    void loadTasks_unknownTaskType_exceptionThrown() throws IOException {
        Path filePath = tempDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "X | 0 | mystery task");
        Storage storage = new Storage(filePath);

        assertThrows(BeemoException.class, storage::loadTasks);
    }

    @Test
    void loadTasks_malformedRecords_exceptionThrown() {
        assertAll(
                () -> assertMalformedRecordRejected("T | 0"),
                () -> assertMalformedRecordRejected("T | maybe | read book"),
                () -> assertMalformedRecordRejected("D | 0 | return book | Sunday"),
                () -> assertMalformedRecordRejected("E | 0 | meeting | Monday | "));
    }

    @Test
    void saveTasks_unknownTaskSubclass_exceptionThrown() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt"));
        Task unsupportedTask = new Task("unsupported");

        assertThrows(BeemoException.class,
                () -> storage.saveTasks(List.of(unsupportedTask)));
    }

    private void assertMalformedRecordRejected(String record) throws IOException {
        Path filePath = tempDirectory.resolve(Integer.toHexString(record.hashCode()) + ".txt");
        Files.writeString(filePath, record);
        Storage storage = new Storage(filePath);

        assertThrows(BeemoException.class, storage::loadTasks);
    }
}
