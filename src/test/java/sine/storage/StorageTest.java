package sine.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import sine.task.Deadline;
import sine.task.Event;
import sine.task.Task;
import sine.task.Todo;

/**
 * Tests task persistence and validation using temporary data files.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    /** Tests first-run loading when no data file exists. */
    @Test
    public void load_fileDoesNotExist_returnsEmptyList() throws IOException {
        Storage storage = new Storage(tempDir.resolve("missing/tasks.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    /** Tests creation of missing parent directories during saving. */
    @Test
    public void save_parentFolderDoesNotExist_createsFolderAndFile() throws IOException {
        Path dataFile = tempDir.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of());

        assertTrue(Files.exists(dataFile));
        assertTrue(Files.readAllLines(dataFile).isEmpty());
    }

    /** Tests a round trip containing every task type and escaped text. */
    @Test
    public void saveAndLoad_allTaskTypesAndEscapedText_preservesTaskData() throws IOException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("compare A | B \\ later");
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 30));
        Event event = new Event("project | meeting", "Mon \\ 2pm", "4pm");
        todo.markAsDone();
        event.markAsDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("compare A | B \\ later", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());
        assertEquals(LocalDate.of(2026, 8, 30), loadedDeadline.getBy());
        assertFalse(loadedDeadline.isDone());
        assertEquals("project | meeting", loadedEvent.getDescription());
        assertEquals("Mon \\ 2pm", loadedEvent.getFrom());
        assertEquals("4pm", loadedEvent.getTo());
        assertTrue(loadedEvent.isDone());
    }

    /** Tests that blank storage lines do not create tasks. */
    @Test
    public void load_fileContainsBlankLines_ignoresBlankLines() throws IOException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.writeString(dataFile, "\nT | 0 | read book\n\n");
        Storage storage = new Storage(dataFile.toString());

        List<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
    }

    /** Tests rejection of malformed storage records. */
    @Test
    public void load_fileContainsMalformedRecord_throwsIOException() throws IOException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        List<String> malformedRecords = List.of(
                "X | 0 | unknown type",
                "T | 2 | invalid status",
                "T | 0",
                "T | 0 | ",
                "D | 0 | impossible date | 2026-02-30",
                "T | 0 | incomplete escape\\");

        for (String record : malformedRecords) {
            Files.writeString(dataFile, record);
            IOException exception = assertThrows(IOException.class, storage::load);
            assertEquals("Invalid task data on line 1", exception.getMessage());
        }
    }
}
