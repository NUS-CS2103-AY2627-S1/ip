package jimbo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jimbo.exception.JimboException;
import jimbo.task.Deadline;
import jimbo.task.Event;
import jimbo.task.Task;
import jimbo.task.Todo;

/**
 * Tests {@link Storage}. Uses a JUnit-managed {@code @TempDir} rather than
 * the real save file, so tests are isolated and repeatable. {@code load()}
 * is also how the private {@code parseLine}/{@code isDoneDigit} methods are
 * exercised, since they are not reachable directly from a test.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void saveThenLoad_mixedTaskTypesAndDoneStatuses_roundTripsCorrectly() throws JimboException {
        Storage storage = new Storage(tempDir.resolve("jimbo.txt").toString());

        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", "2/12/2019 1800");
        deadline.markAsDone();
        Event event = new Event("team meeting", "3/12/2019 1400", "3/12/2019 1600");

        ArrayList<Task> original = new ArrayList<>();
        original.add(todo);
        original.add(deadline);
        original.add(event);
        storage.save(original);

        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals(todo.toSaveFormat(), loaded.get(0).toSaveFormat());
        assertEquals(deadline.toSaveFormat(), loaded.get(1).toSaveFormat());
        assertEquals(event.toSaveFormat(), loaded.get(2).toSaveFormat());
    }

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("does-not-exist.txt").toString());

        ArrayList<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
    }

    @Test
    public void save_parentDirectoryMissing_createsDirectoryAndSavesFile() {
        Storage storage = new Storage(tempDir.resolve("nested/data/jimbo.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        storage.save(tasks);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
    }

    @Test
    public void load_corruptedLine_skipsLineAndKeepsValidOnes() throws IOException {
        Path file = tempDir.resolve("jimbo.txt");
        Files.writeString(file, "T | 0 | valid task" + System.lineSeparator()
                + "Z | 0 | unknown type icon" + System.lineSeparator());
        Storage storage = new Storage(file.toString());

        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("valid task", loaded.get(0).getDescription());
    }

    @Test
    public void load_blankLinesInFile_areIgnored() throws IOException {
        Path file = tempDir.resolve("jimbo.txt");
        Files.writeString(file, System.lineSeparator() + "T | 0 | valid task" + System.lineSeparator()
                + "   " + System.lineSeparator());
        Storage storage = new Storage(file.toString());

        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("valid task", loaded.get(0).getDescription());
    }
}
