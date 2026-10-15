import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tardt.exception.TardTException;
import tardt.priority.Priority;
import tardt.storage.Storage;
import tardt.task.Deadline;
import tardt.task.Event;
import tardt.task.Task;
import tardt.task.ToDo;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void saveThenLoad_mixedTaskTypes_preservesDataAndCompletionStatus() throws TardTException {
        Path saveFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(saveFile);

        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit assignment", "2026-10-20T17:00");
        Event event = new Event("lecture", "2026-10-20T15:00", "2026-10-20T16:00");

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());

        assertEquals("read book", loaded.get(0).getDescription());
        assertTrue(loaded.get(0).isDone());

        assertTrue(loaded.get(1) instanceof Deadline);
        assertEquals("submit assignment", loaded.get(1).getDescription());
        assertEquals(false, loaded.get(1).isDone());

        assertTrue(loaded.get(2) instanceof Event);
        assertEquals("lecture", loaded.get(2).getDescription());
    }

    @Test
    public void load_fileDoesNotExist_returnsEmptyListWithoutThrowing() throws TardTException {
        Path saveFile = tempDir.resolve("does-not-exist.txt");
        Storage storage = new Storage(saveFile);

        List<Task> loaded = storage.load();

        assertNotNull(loaded);
        assertEquals(0, loaded.size());
    }

    @Test
    public void load_fileWithMalformedAndValidLines_skipsMalformedLinesOnly() throws IOException, TardTException {
        Path saveFile = tempDir.resolve("tasks.txt");
        // "X | 0 | bad type" -> unrecognised type, should be skipped
        // "D | 0 | missing by field" -> too few parts for a Deadline, should be skipped
        // "T | 0 | valid todo" -> should load correctly
        String fileContents = String.join(System.lineSeparator(),
                "X | 0 | bad type",
                "D | 0 | missing by field",
                "T | 0 | valid todo");
        Files.writeString(saveFile, fileContents);

        Storage storage = new Storage(saveFile);
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("valid todo", loaded.get(0).getDescription());
    }

    @Test
    public void saveThenLoad_descriptionWithPipeAndBackslash_preservesDescriptionAndPriority() throws TardTException {
        Path saveFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(saveFile);
        ToDo todo = new ToDo("review | save to C:\\tasks", Priority.HIGH);

        storage.save(List.of(todo));
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("review | save to C:\\tasks", loaded.get(0).getDescription());
        assertEquals(Priority.HIGH, loaded.get(0).getPriority());
    }

    @Test
    public void load_legacyDescriptionWithPipeAndPriority_recoversDescriptionAndPriority()
            throws IOException, TardTException {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile, "T | 0 | review | old format | high");
        Storage storage = new Storage(saveFile);

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("review | old format", loaded.get(0).getDescription());
        assertEquals(Priority.HIGH, loaded.get(0).getPriority());
    }
}
