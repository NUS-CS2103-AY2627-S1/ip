package braun.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import braun.exception.BraunException;
import braun.task.Deadline;
import braun.task.Event;
import braun.task.Task;
import braun.task.Todo;

/**
 * Unit tests for {@link Storage} file persistence and parsing logic.
 */
public class StorageTest {

    @Test
    public void parseTask_validTodoUndone_success() throws BraunException {
        Task task = Storage.parseTask("T | 0 | read book");
        assertTrue(task instanceof Todo);
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals("T | 0 | read book", task.toFileFormat());
    }

    @Test
    public void parseTask_validTodoDone_success() throws BraunException {
        Task task = Storage.parseTask("T | 1 | return library book");
        assertTrue(task instanceof Todo);
        assertEquals("return library book", task.getDescription());
        assertTrue(task.isDone());
        assertEquals("T | 1 | return library book", task.toFileFormat());
    }

    @Test
    public void parseTask_validDeadline_success() throws BraunException {
        Task task = Storage.parseTask("D | 0 | submit report | 2026-08-30 1700");
        assertTrue(task instanceof Deadline);
        assertEquals("submit report", task.getDescription());
        assertFalse(task.isDone());
        assertEquals("D | 0 | submit report | 2026-08-30 1700", task.toFileFormat());
    }

    @Test
    public void parseTask_validEvent_success() throws BraunException {
        Task task = Storage.parseTask("E | 1 | company retreat | 2026-08-24 1400 | 2026-08-24 1600");
        assertTrue(task instanceof Event);
        assertEquals("company retreat", task.getDescription());
        assertTrue(task.isDone());
        assertEquals("E | 1 | company retreat | 2026-08-24 1400 | 2026-08-24 1600", task.toFileFormat());
    }

    @Test
    public void parseTask_unknownType_exceptionThrown() {
        assertThrows(BraunException.class, () -> Storage.parseTask("X | 0 | unknown task"));
    }

    @Test
    public void parseTask_invalidStatus_exceptionThrown() {
        assertThrows(BraunException.class, () -> Storage.parseTask("T | 2 | invalid status"));
    }

    @Test
    public void parseTask_insufficientFields_exceptionThrown() {
        assertThrows(BraunException.class, () -> Storage.parseTask("T | 0"));
    }

    @Test
    public void parseTask_emptyDescription_exceptionThrown() {
        assertThrows(BraunException.class, () -> Storage.parseTask("T | 0 |   "));
    }

    @Test
    public void saveAndLoad_multipleTasks_restoredAccurately(@TempDir Path tempDir) throws BraunException {
        Path testFile = tempDir.resolve("test-tasks.txt");
        Storage storage = new Storage(testFile.toString());

        ArrayList<Task> originalTasks = new ArrayList<>();
        originalTasks.add(new Todo("buy groceries"));
        Deadline deadline = new Deadline("submit claim", "2026-08-30 1700");
        deadline.markAsDone();
        originalTasks.add(deadline);
        originalTasks.add(new Event("team meeting", "2026-08-24 1400", "2026-08-24 1600"));

        storage.save(originalTasks);

        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(3, loadedTasks.size());

        assertEquals(originalTasks.get(0).toFileFormat(), loadedTasks.get(0).toFileFormat());
        assertEquals(originalTasks.get(1).toFileFormat(), loadedTasks.get(1).toFileFormat());
        assertEquals(originalTasks.get(2).toFileFormat(), loadedTasks.get(2).toFileFormat());
    }
}
