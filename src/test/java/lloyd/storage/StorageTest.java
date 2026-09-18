package lloyd.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lloyd.task.Deadline;
import lloyd.task.Event;
import lloyd.task.Task;
import lloyd.task.Todo;

/**
 * Tests saving and loading task data through {@link Storage}.
 */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    /** Verifies that saving and loading preserve mixed task data and order. */
    @Test
    public void saveAndLoad_mixedTasks_preservesTaskDataAndOrder() throws IOException {
        Todo todo = new Todo("read book");
        todo.mark();
        Deadline deadline = new Deadline(
                "return book", LocalDate.of(2026, 9, 6));
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 7, 14, 0),
                LocalDateTime.of(2026, 9, 7, 16, 0));
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt"));

        storage.save(List.of(todo, deadline, event));
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read book", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 6), loadedDeadline.getBy());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 7, 14, 0), loadedEvent.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 7, 16, 0), loadedEvent.getTo());
    }

    /** Verifies that loading from a missing path creates an empty storage file. */
    @Test
    public void load_missingFile_createsEmptyStorageFile() throws IOException {
        Path filePath = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertTrue(Files.isRegularFile(filePath));
    }

    /** Verifies that blank storage lines are ignored during loading. */
    @Test
    public void load_blankLines_ignoresBlankLines() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.write(filePath, List.of("", "T | 0 | read book", "   "),
                StandardCharsets.UTF_8);

        ArrayList<Task> loadedTasks = new Storage(filePath).load();

        assertEquals(1, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getDescription());
    }

    /** Verifies that malformed saved data reports the offending line number. */
    @Test
    public void load_invalidTaskData_throwsIoExceptionWithLineNumber() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.write(filePath, List.of(
                "T | 0 | valid task",
                "D | 0 | invalid deadline | not-a-date"), StandardCharsets.UTF_8);

        IOException exception = assertThrows(IOException.class, () ->
                new Storage(filePath).load());

        assertTrue(exception.getMessage().contains("line 2"));
    }

    /** Verifies that saving rejects a null task list. */
    @Test
    public void save_nullTaskList_throwsIllegalArgumentException() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));

        assertThrows(IllegalArgumentException.class, () -> storage.save(null));
    }

    /** Verifies that saving rejects a null task within the list. */
    @Test
    public void save_nullTask_throwsIllegalArgumentException() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        List<Task> tasks = new ArrayList<>();
        tasks.add(null);

        assertThrows(IllegalArgumentException.class, () -> storage.save(tasks));
    }

    /** Verifies that invalid delimiters are rejected before replacing saved data. */
    @Test
    public void save_reservedDelimiter_preservesExistingFile() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "T | 0 | original", StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        assertThrows(IllegalArgumentException.class, () ->
                storage.save(List.of(new Todo("compare A | B"))));

        assertEquals("T | 0 | original",
                Files.readString(filePath, StandardCharsets.UTF_8));
    }

    /** Verifies that unsupported task types do not replace existing saved data. */
    @Test
    public void save_unsupportedTaskType_preservesExistingFile() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "T | 0 | original", StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        assertThrows(IllegalArgumentException.class, () ->
                storage.save(List.of(new Task("unsupported"))));

        assertEquals("T | 0 | original",
                Files.readString(filePath, StandardCharsets.UTF_8));
    }
}
