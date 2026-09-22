package wiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void saveAndLoad_multipleTasks_savedAndLoadedCorrectly() throws IOException {
        Path testFile = tempDir.resolve("test_tasks.txt");
        Storage storage = new Storage(testFile.toString());

        ArrayList<Task> tasksToSave = new ArrayList<>();
        tasksToSave.add(new ToDo("read book"));
        tasksToSave.add(new Deadline("return book", LocalDateTime.of(2026, 9, 10, 18, 0)));
        tasksToSave.add(new Event(
                "orientation",
                LocalDateTime.of(2026, 9, 11, 9, 0),
                LocalDateTime.of(2026, 9, 11, 12, 0)
        ));

        tasksToSave.get(1).markAsDone();

        storage.save(tasksToSave);

        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(3, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(1).isDone());
        assertEquals("orientation", loadedTasks.get(2).getDescription());
    }

    @Test
    public void load_fileDoesNotExist_emptyListReturned() throws IOException {
        Path nonExistentFile = tempDir.resolve("non_existent.txt");
        Storage storage = new Storage(nonExistentFile.toString());

        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(0, loadedTasks.size());
    }

    @Test
    public void parseTask_invalidFormat_throwsIOException() {
        Storage storage = new Storage("dummy.txt");
        assertThrows(IOException.class, () -> storage.parseTask("invalid"));
        assertThrows(IOException.class, () -> storage.parseTask("D | 0 | return book"));
        assertThrows(IOException.class, () -> storage.parseTask("E | 0 | event | 2026-09-11 0900"));
        assertThrows(IOException.class, () -> storage.parseTask("X | 0 | unknown"));
    }

    @Test
    public void parseTask_invalidDate_throwsIOException() {
        Storage storage = new Storage("dummy.txt");
        assertThrows(IOException.class, () ->
                storage.parseTask("D | 0 | deadline | invalid-date"));
        assertThrows(IOException.class, () ->
                storage.parseTask("D | 0 | deadline | 2026-02-30 1800"));
    }

    @Test
    public void parseTask_eventEndBeforeStart_throwsIOException() {
        Storage storage = new Storage("dummy.txt");
        assertThrows(IOException.class, () ->
                storage.parseTask("E | 0 | event | 2026-09-12 1800 | 2026-09-12 1000"));
    }
}
