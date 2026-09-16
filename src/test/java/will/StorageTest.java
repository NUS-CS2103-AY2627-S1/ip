package will;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import will.task.Deadline;
import will.task.Event;
import will.task.Task;
import will.task.Todo;

public class StorageTest {

    @Test
    public void load_fileDoesNotExist_returnsEmptyList(@org.junit.jupiter.api.io.TempDir Path tempDir) {
        Storage storage = new Storage(tempDir.resolve("does-not-exist.txt"));
        ArrayList<Task> loaded = storage.load(new Ui());
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void saveThenLoad_mixOfTaskTypes_roundTripsCorrectly(@org.junit.jupiter.api.io.TempDir Path tempDir)
            throws WillException {
        Storage storage = new Storage(tempDir.resolve("will.txt"));
        ArrayList<Task> original = new ArrayList<>();
        original.add(new Todo("read book"));
        original.add(new Deadline("return book", "2019-10-15"));
        Event event = new Event("project meeting", "2019-10-20", "2019-10-21");
        event.markAsDone();
        original.add(event);

        storage.save(original);
        ArrayList<Task> loaded = storage.load(new Ui());

        assertEquals(3, loaded.size());
        assertEquals(original.get(0).toSaveFormat(), loaded.get(0).toSaveFormat());
        assertEquals(original.get(1).toSaveFormat(), loaded.get(1).toSaveFormat());
        assertEquals(original.get(2).toSaveFormat(), loaded.get(2).toSaveFormat());
    }

    @Test
    public void save_parentFolderDoesNotExistYet_createsItAutomatically(@org.junit.jupiter.api.io.TempDir Path tempDir)
            throws WillException {
        Storage storage = new Storage(tempDir.resolve("nested/data/will.txt"));
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        storage.save(tasks);

        assertTrue(Files.exists(tempDir.resolve("nested/data/will.txt")));
    }

    @Test
    public void load_corruptedLineAmongValidOnes_skipsOnlyTheCorruptedLine(@org.junit.jupiter.api.io.TempDir
            Path tempDir) throws IOException {
        Path file = tempDir.resolve("will.txt");
        Files.writeString(file, "T | 0 | read book\n"
                + "not a valid line\n"
                + "T | 1 | buy milk\n");
        Storage storage = new Storage(file);

        ArrayList<Task> loaded = storage.load(new Ui());

        assertEquals(2, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals("buy milk", loaded.get(1).getDescription());
    }

    @Test
    public void load_blankLines_areSkippedSilently(@org.junit.jupiter.api.io.TempDir Path tempDir)
            throws IOException {
        Path file = tempDir.resolve("will.txt");
        Files.writeString(file, "T | 0 | read book\n\n   \n");
        Storage storage = new Storage(file);

        ArrayList<Task> loaded = storage.load(new Ui());

        assertEquals(1, loaded.size());
    }
}
