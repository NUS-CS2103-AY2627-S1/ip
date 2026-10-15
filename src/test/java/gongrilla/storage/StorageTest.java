package gongrilla.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gongrilla.task.Task;
import gongrilla.task.Todo;

/**
 * Tests journal invariants and validation with Java assertions enabled.
 */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_journalOperations_restoresCompletionAndDeletion() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        Todo completed = new Todo("completed");
        completed.markDone();
        storage.appendAdd(completed);
        storage.appendAdd(new Todo("pending"));
        assertTrue(storage.load().get(0).isDone());
        assertFalse(storage.load().get(1).isDone());

        storage.appendUnmark(0);
        storage.appendMark(1);
        List<Task> restored = storage.load();
        assertFalse(restored.get(0).isDone());
        assertTrue(restored.get(1).isDone());

        storage.appendDelete(0);
        assertEquals("pending", storage.load().get(0).getName());
        assertEquals(1, storage.load().size());
    }

    @Test
    void appendAdd_descriptionWithLineBreaks_writesOneRecordAndRoundTrips() throws IOException {
        Path path = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(path);
        String description = "first\nsecond\rthird | fourth";

        storage.appendAdd(new Todo(description));

        assertEquals(1, Files.readAllLines(path).size());
        assertEquals(description, storage.load().get(0).getName());
    }

    @Test
    void appendAdd_brokenSerializer_assertsBeforeWriting() {
        Path path = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(path);
        Task brokenTask = new Todo("broken") {
            @Override
            public String toDataString() {
                return "T2 | 0 | first\nT2 | 0 | second";
            }
        };

        assertThrows(AssertionError.class, () -> storage.appendAdd(brokenTask));
        assertFalse(Files.exists(path));
    }

    @Test
    void load_invalidExternalRecord_throwsIoException() throws IOException {
        Path path = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(path);
        Files.writeString(path, "T2 | 2 | invalid completion\n");

        assertThrows(IOException.class, storage::load);
        assertThrows(IllegalArgumentException.class, () -> storage.appendMark(-1));
    }
}
