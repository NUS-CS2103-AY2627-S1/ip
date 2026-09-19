package bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests loading, validation, persistence, and completion restoration. */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void missingFile_loadsEmptyList() throws BobException {
        assertTrue(new Storage(temporaryDirectory.resolve("missing.txt").toString()).load().isEmpty());
    }

    @Test
    public void saveAndLoad_roundTripsAllTaskTypes() throws Exception {
        Path file = temporaryDirectory.resolve("nested/tasks.txt");
        Task todo = new Task("Read");
        todo.markDone();
        List<Task> original = List.of(todo,
                new Deadline("Submit", java.time.LocalDate.of(2026, 9, 1)),
                new Event("Meeting", java.time.LocalDate.of(2026, 9, 1),
                        java.time.LocalDate.of(2026, 9, 2)));

        new Storage(file.toString()).save(original);
        List<Task> loaded = new Storage(file.toString()).load();

        assertEquals(original.stream().map(Task::toStorageString).toList(),
                loaded.stream().map(Task::toStorageString).toList());
        assertTrue(loaded.get(0).isDone());
    }

    @Test
    public void invalidFileData_throwsHelpfulException() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "X | 0 | Unknown\n");

        BobException exception = assertThrows(BobException.class, () -> new Storage(file.toString()).load());

        assertTrue(exception.getMessage().contains("line 1"));
    }

    @Test
    public void duplicateFileData_isRejected() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | Same\nT | 0 | Same\n");

        assertThrows(BobException.class, () -> new Storage(file.toString()).load());
    }

    @Test
    public void save_invalidInput_isRejected() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ArrayList<Task> tasksWithNull = new ArrayList<>();
        tasksWithNull.add(new Task("valid"));
        tasksWithNull.add(null);

        assertThrows(BobException.class, () -> storage.save(null));
        assertThrows(BobException.class, () -> storage.save(tasksWithNull));
        assertFalse(Files.exists(temporaryDirectory.resolve("tasks.txt")));
    }
}
