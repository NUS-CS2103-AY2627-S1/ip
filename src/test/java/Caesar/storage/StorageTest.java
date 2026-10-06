package caesar.storage;

import caesar.exception.CaesarException;
import caesar.task.Deadline;
import caesar.task.Event;
import caesar.task.Task;
import caesar.task.ToDo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void testLoadCreatesMissingFileAndParentDirectories() throws CaesarException {
        Path taskFile = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(taskFile);

        assertTrue(storage.load().isEmpty());
        assertTrue(Files.exists(taskFile));
        assertTrue(storage.wasFileCreated());

        storage.load();
        assertFalse(storage.wasFileCreated());
    }

    @Test
    public void testSaveAndLoadAllTaskTypesAndStatuses() throws CaesarException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(taskFile);
        ArrayList<Task> tasks = new ArrayList<>();

        ToDo todo = new ToDo("Read a book");
        todo.markAsDone();
        tasks.add(todo);
        tasks.add(new Deadline("Submit report", "Jun 1 2024"));
        tasks.add(new Event("Project meeting", "Jun 2 2024", "Jun 3 2024"));

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(ToDo.class, loadedTasks.get(0));
        assertTrue(loadedTasks.get(0).isDone());
        assertEquals("Read a book", loadedTasks.get(0).getDescription());
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("Jun 1 2024", ((Deadline) loadedTasks.get(1)).getBy());
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("Jun 2 2024", ((Event) loadedTasks.get(2)).getStart());
        assertEquals("Jun 3 2024", ((Event) loadedTasks.get(2)).getEnd());
    }

    @Test
    public void testLoadIgnoresBlankLines() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "\nT | 0 | Read a book\n   \n");

        ArrayList<Task> tasks = new Storage(taskFile).load();

        assertEquals(1, tasks.size());
        assertEquals("Read a book", tasks.get(0).getDescription());
    }

    @Test
    public void testLoadRejectsUnknownTaskType() {
        CaesarException exception = assertInvalidFile("X | 0 | Task");

        assertEquals("Invalid task data on line 1: unknown task type X", exception.getMessage());
    }

    @Test
    public void testLoadRejectsWrongNumberOfFields() {
        CaesarException exception = assertInvalidFile("T | 0 | Task | extra");

        assertEquals("Invalid task data on line 1: expected 3 fields but found 4",
                exception.getMessage());
    }

    @Test
    public void testLoadRejectsInvalidStatus() {
        CaesarException exception = assertInvalidFile("T | pending | Task");

        assertEquals("Invalid task data on line 1: status must be 1 or 0", exception.getMessage());
    }

    @Test
    public void testLoadRejectsEmptyDescription() {
        CaesarException exception = assertInvalidFile("T | 0 |   ");

        assertEquals("Invalid task data on line 1: description cannot be empty",
                exception.getMessage());
    }

    private CaesarException assertInvalidFile(String content) {
        Path taskFile = temporaryDirectory.resolve("invalid.txt");
        try {
            Files.writeString(taskFile, content);
        } catch (Exception exception) {
            throw new AssertionError("Could not create test task file", exception);
        }

        return assertThrows(CaesarException.class, () -> new Storage(taskFile).load());
    }
}
