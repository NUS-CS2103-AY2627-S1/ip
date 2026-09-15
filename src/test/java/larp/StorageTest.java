package larp;

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

/**
 * Tests task persistence performed by {@link Storage}.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveAndLoad_mixedTasks_preservesTaskData() throws LarpException {
        Path dataFile = this.temporaryDirectory.resolve("data/larp.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(List.of(
                todo,
                new Deadline("submit report", LocalDate.of(2026, 8, 31)),
                new Event("project meeting", "2pm", "3pm", true)));

        storage.save(tasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());

        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read book", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("submit report", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 31), loadedDeadline.getBy());
        assertFalse(loadedDeadline.isDone());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals("2pm", loadedEvent.getFrom());
        assertEquals("3pm", loadedEvent.getTo());
        assertTrue(loadedEvent.isDone());
    }

    @Test
    public void load_missingFile_createsEmptyFile() throws LarpException {
        Path dataFile = this.temporaryDirectory.resolve("nested/larp.txt");
        Storage storage = new Storage(dataFile.toString());

        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertTrue(Files.exists(dataFile));
    }

    @Test
    public void load_invalidCompletionStatus_throwsLarpException() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("larp.txt");
        Files.writeString(dataFile, "T | yes | read book");
        Storage storage = new Storage(dataFile.toString());

        assertThrows(LarpException.class, storage::load);
    }

    @Test
    public void load_invalidDeadlineDate_throwsLarpException() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("larp.txt");
        Files.writeString(dataFile, "D | 0 | submit report | 2026-02-30");
        Storage storage = new Storage(dataFile.toString());

        assertThrows(LarpException.class, storage::load);
    }

    @Test
    public void load_unknownTaskType_throwsLarpException() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("larp.txt");
        Files.writeString(dataFile, "X | 0 | mystery task");
        Storage storage = new Storage(dataFile.toString());

        assertThrows(LarpException.class, storage::load);
    }

    @Test
    public void load_wrongFieldCount_throwsLarpException() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("larp.txt");
        Files.writeString(dataFile, "E | 0 | project meeting | 2pm");
        Storage storage = new Storage(dataFile.toString());

        assertThrows(LarpException.class, storage::load);
    }

    @Test
    public void save_unknownTaskType_throwsLarpException() {
        Path dataFile = this.temporaryDirectory.resolve("larp.txt");
        Storage storage = new Storage(dataFile.toString());
        TaskList tasks = new TaskList(List.of(new Task("unsupported task")));

        assertThrows(LarpException.class, () -> storage.save(tasks));
    }
}
